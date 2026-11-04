package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByUserUsername(String username);
}
