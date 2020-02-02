package com.pchudzik.blog.example.readfromreplica;

import com.pchudzik.blog.example.readfromreplica.model.Task;
import com.pchudzik.blog.example.readfromreplica.model.TaskRepository;
import com.pchudzik.blog.example.readfromreplica.model.TaskServiceOuter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ReadFromReplicaApplication {
    private static final Logger log = LoggerFactory.getLogger(ReadFromReplicaApplication.class);

    public static void main(String [] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ReadFromReplicaApplication.class, args);
        TaskServiceOuter service = ctx.getBean(TaskServiceOuter.class);

        log.info("save & findAll {}", service.findAllReadwrite());
    }
    public static void simpleMain(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ReadFromReplicaApplication.class, args);
        final TaskRepository taskRepository = ctx.getBean(TaskRepository.class);

        taskRepository.save(new Task("first", "Some task 1"));
        log.info("all tasks: {}", taskRepository.findAll());
        taskRepository.save(new Task("second", "Some task 2"));
        log.info("all tasks: {}", taskRepository.findAll());
    }

    public static void complexMain(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ReadFromReplicaApplication.class, args);
        final TaskRepository taskRepository = ctx.getBean(TaskRepository.class);
        final ExecutorService executorService = Executors.newFixedThreadPool(7);
        for (int i = 0; i < 100; i++) {
            executorService.submit(new Writer(taskRepository, i));
            if (i % 3 == 0) {
                executorService.submit(new Reader(taskRepository));
            }
        }
        executorService.shutdown();
        new Reader(taskRepository).run();
    }

    private static class Writer implements Runnable {
        private final TaskRepository taskRepository;
        private final int index;

        private Writer(TaskRepository taskRepository, int index) {
            this.taskRepository = taskRepository;
            this.index = index;
        }

        @Override
        public void run() {
            taskRepository.save(new Task("task " + index, "some description " + index));
        }
    }

    private static class Reader implements Runnable {
        private static final Logger log = LoggerFactory.getLogger(Reader.class);
        private final TaskRepository taskRepository;

        private Reader(TaskRepository taskRepository) {
            this.taskRepository = taskRepository;
        }

        @Override
        public void run() {
            log.info("Number of entries in db is {}", taskRepository.findAll().size());
        }
    }
}
