package com.example.taskmanager;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
