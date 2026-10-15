package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.enums.VerificationStatus;
import com.springboot.careercrafter.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {

    List<Employer> findByVerificationStatus(VerificationStatus status);

    // select e from Employer e where e.user.username = ?1
    Optional<Employer> findByUserUsername(String username);

    @Query("""
            select e from Employer e
            where (?1 is null or e.verificationStatus = ?1)
            and (?2 is null or e.companyName = ?2)
            and (?3 is null or e.fullName = ?3)
            """)
    List<Employer> filterEmployers(VerificationStatus status, String companyName, String fullName);
}