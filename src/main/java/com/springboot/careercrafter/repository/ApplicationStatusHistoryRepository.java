package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.model.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {

    // select h from ApplicationStatusHistory h where h.jobApplication.id = ?1 order by h.changedAt asc
    List<ApplicationStatusHistory> findByJobApplicationIdOrderByChangedAtAsc(Long jobApplicationId);

    @Modifying
    @Query("delete from ApplicationStatusHistory h where h.jobApplication.id = ?1")
    void deleteByJobApplicationId(Long jobApplicationId);
}