package com.pchudzik.blog.example.readfromreplica.model;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TaskServiceInner {
    private final TaskRepository taskRepository;

    public TaskServiceInner(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> findAllReadonly() {
        return taskRepository.findAll();
    }

    @Transactional
    public void save(Task task) {
        taskRepository.saveAndFlush(task);
    }
}
