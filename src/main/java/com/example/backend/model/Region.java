package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;  // Example: Bihar, Kerala, Kolkata, Punjab, Rajasthan

    @Column(columnDefinition = "TEXT")
    private String description;  // Optional: cultural details, history, specialties
}
