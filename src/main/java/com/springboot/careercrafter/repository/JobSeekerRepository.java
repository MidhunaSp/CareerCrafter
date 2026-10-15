package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {

    Optional<JobSeeker> findByUserUsername(String username);

    @Query("""
            select js from JobSeeker js
            where (?1 is null or js.fullName = ?1)
            and (?2 is null or js.location = ?2)
            and (?3 is null or js.skills = ?3)
            and (?4 is null or js.education = ?4)
            """)
    List<JobSeeker> filterJobSeekers(String fullName, String location, String skills, String education);
}