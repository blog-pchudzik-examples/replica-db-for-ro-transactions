package com.pchudzik.blog.example.readfromreplicat.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
