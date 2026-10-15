package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            select u
            from User u
            where u.username=?1 AND u.isActive = ?2
            """)
    User loadUserByUsername(String username,boolean isActive);

    boolean existsByUsername(String username);
}
