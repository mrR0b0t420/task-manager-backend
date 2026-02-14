package com.example.taskmanager.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a subtask within a main task.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore // Prevent recursion
    private Task task;
}
