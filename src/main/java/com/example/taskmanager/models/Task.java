package com.example.taskmanager.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a task in the system.
 * This entity maps to the "task" table in the database.
 * 
 * It includes details such as title, category, priority, and status.
 * 
 * @author Nexus Supply Chain Dev Team
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Category category;

    private int priority; // 1-5

    // private int lifespanHours; // Removed

    private LocalDateTime createdAt;

    /**
     * The user who owns this task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User user;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Subtask> subtasks = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Comment> comments = new java.util.ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TaskStatus.TODO;
        }
    }

    // derived getter removed

    public enum Category {
        WORK, HOME, PERSONAL
    }
}
