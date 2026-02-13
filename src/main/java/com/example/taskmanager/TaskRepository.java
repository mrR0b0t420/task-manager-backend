package com.example.taskmanager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCategory(Task.Category category);

    List<Task> findByStatus(TaskStatus status);
}
