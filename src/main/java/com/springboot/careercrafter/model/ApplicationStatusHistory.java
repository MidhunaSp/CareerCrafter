package com.springboot.careercrafter.model;
import com.springboot.careercrafter.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(length = 500)
    private String note;

    @CreationTimestamp
    private Instant changedAt;

    @ManyToOne
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;
}
