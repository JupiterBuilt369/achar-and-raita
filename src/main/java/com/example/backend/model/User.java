package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Storing plain text for MVP (Upgrade to BCrypt later)

    @Column(nullable = false)
    private String phone;

    // Future: @Enumerated(EnumType.STRING) private UserRole role;
}