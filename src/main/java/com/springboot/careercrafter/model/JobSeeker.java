package com.springboot.careercrafter.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "job_seekers")
public class JobSeeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String phone;
    @Column(length = 150)
    private String location;
    private String education;
    @Column(nullable = false)
    private String skills;
    @Column(nullable = false)
    private String experience;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}