package com.springboot.careercrafter.model;

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
@Table(name = "saved_jobs")
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Instant savedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private JobSeeker jobSeeker;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}