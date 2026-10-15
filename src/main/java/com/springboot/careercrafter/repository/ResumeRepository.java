package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // select r from Resume r where r.jobSeeker.id = ?1 order by r.uploadedAt desc
    List<Resume> findByJobSeekerIdOrderByUploadedAtDesc(Long jobSeekerId);
}