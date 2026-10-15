package com.springboot.careercrafter.model;

import com.springboot.careercrafter.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employers")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, length=200)
    private String companyName;
    @Column(nullable = false, length=10)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus  verificationStatus;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}