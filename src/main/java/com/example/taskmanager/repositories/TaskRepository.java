package com.example.taskmanager.repositories;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.TaskStatus;
import com.example.taskmanager.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Task entity.
 * Extends JpaRepository to provide standard CRUD operations.
 * 
 * Includes custom finder methods for filtering by Category and Status.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Finds all tasks with the given category.
     * 
     * @param category The category to filter by (WORK, HOME, PERSONAL).
     * @return List of matching tasks.
     */
    List<Task> findByCategory(Task.Category category);

    /**
     * Finds all tasks with the given status.
     * 
     * @param status The status to filter by (TODO, IN_PROGRESS, DONE).
     * @return List of matching tasks.
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Finds all tasks owned by a specific user.
     * 
     * @param user The owner of the tasks.
     * @return List of tasks.
     */
    List<Task> findByUser(User user);

    List<Task> findByUserId(Long userId);
}
