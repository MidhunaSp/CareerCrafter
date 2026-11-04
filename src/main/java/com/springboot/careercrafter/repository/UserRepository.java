package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.dto.response.DetailsDto;
import com.springboot.careercrafter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            select u
            from User u
            where u.username=?1 
            """)
    User loadUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
    select u.username,u.isActive,u.role
    from User u
    where u.username=?1
            """)
    DetailsDto getUserDetails(String loggedInUsername);
}
