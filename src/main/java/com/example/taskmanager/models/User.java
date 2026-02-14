package com.example.taskmanager.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a user in the system.
 * 
 * Roles:
 * - ADMIN: Can manage all tasks and users.
 * - USER: Can manage only their own tasks.
 */
@Entity
@Table(name = "users") // 'user' is a reserved keyword in Postgres
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;

    @Column(nullable = false)
    private String role; // "ADMIN" or "USER"
}
