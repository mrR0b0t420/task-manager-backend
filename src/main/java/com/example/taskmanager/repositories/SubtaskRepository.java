package com.example.taskmanager.repositories;

import com.example.taskmanager.models.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByTaskId(Long taskId);
}
