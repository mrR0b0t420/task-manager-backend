package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.TaskStatus;
import com.example.taskmanager.models.User;
import com.example.taskmanager.repositories.TaskRepository;
import com.example.taskmanager.repositories.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void testGetAllTasks_User() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setRole("USER");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setUser(user);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(Arrays.asList(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testGetAllTasks_Admin() throws Exception {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Admin Task");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Admin Task"));
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void testCreateTask() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        Task task = new Task();
        task.setTitle("New Task");
        task.setStatus(TaskStatus.TODO);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }
}
