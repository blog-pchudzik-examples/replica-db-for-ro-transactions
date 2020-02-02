package com.pchudzik.blog.example.readfromreplica.model;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TaskServiceOuter {
    private final TaskServiceInner taskServiceInner;

    public TaskServiceOuter(TaskServiceInner taskServiceInner) {
        this.taskServiceInner = taskServiceInner;
    }

    @Transactional(readOnly = true)
    public List<Task> findAllReadonly() {
        taskServiceInner.save(new Task("a task", "some description"));
        return taskServiceInner.findAllReadonly();
    }

    @Transactional
    public List<Task> findAllReadwrite() {
        taskServiceInner.save(new Task("a task", "some description"));
        return taskServiceInner.findAllReadonly();
    }
}
