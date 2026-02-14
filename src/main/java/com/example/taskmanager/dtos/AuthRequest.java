package com.example.taskmanager.dtos;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
