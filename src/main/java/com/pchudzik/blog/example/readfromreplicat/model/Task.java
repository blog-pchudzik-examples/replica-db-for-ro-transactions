package com.pchudzik.blog.example.readfromreplicat.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Task {
    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
