package com.springboot.careercrafter.repository;

import com.springboot.careercrafter.dto.response.NotificationRespDto;
import com.springboot.careercrafter.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // n = notification, u = n.user
    @Query("""
            select new com.springboot.careercrafter.dto.response.NotificationRespDto(
                n.id, u.id, n.message, n.type, n.isRead, n.createdAt)
            from Notification n
            JOIN n.user u
            where u.id = ?1
            order by n.createdAt desc
            """)
    List<NotificationRespDto> getByUserId(Long userId);
}