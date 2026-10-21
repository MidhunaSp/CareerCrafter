package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.dto.response.ApplicationStatusStatDto;
import com.springboot.careercrafter.enums.ApplicationStatus;
import com.springboot.careercrafter.model.JobApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // select ja from JobApplication ja where ja.job.id = ?1 and ja.applicant.id = ?2
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);

    @Query("""
            select ja from JobApplication ja
            where ja.applicant.id = ?1
            order by ja.appliedAt desc
            """)
    List<JobApplication> findByApplicantIdWithDetails(Long applicantId, Pageable pageable);

    @Query("""
            select ja from JobApplication ja
            where ja.job.id = ?1
            and (?2 is null or ja.status = ?2)
            order by ja.appliedAt desc
            """)
    List<JobApplication> findByJobIdWithDetails(Long jobId, ApplicationStatus status, Pageable pageable);

    @Query("""
            select ja from JobApplication ja
            where ja.id = ?1
            """)
    Optional<JobApplication> findDetailsById(Long id);


    @Query("""
            select new com.springboot.careercrafter.dto.response.ApplicationStatusStatDto(ja.status, count(ja.id))
            from JobApplication ja
            group by ja.status
            """)
    List<ApplicationStatusStatDto> getApplicationStatusStat();

    @Query("""
        select ja from JobApplication ja
        where ja.job.employer.user.username = ?1
        order by ja.appliedAt desc
        """)
    List<JobApplication> findAllByEmployerUsername(String username);

}