package com.example.taskmanager;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

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
