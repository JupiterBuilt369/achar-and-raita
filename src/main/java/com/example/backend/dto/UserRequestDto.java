package com.example.backend.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String name;
    private String email;
    private String phone;
    private String password;  // accept password
}
