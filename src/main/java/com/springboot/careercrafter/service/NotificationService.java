package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.NotificationReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.NotificationRespDto;
import com.springboot.careercrafter.exception.OwnershipException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.model.Notification;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.NotificationRepository;
import com.springboot.careercrafter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // called by the ADMIN (SecurityConfig), so no ownership check here
    public CreatedRespDto add(Long userId, NotificationReqDto dto) {

        // step 1: fetch the user the notification is for
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User id invalid & User not found"));

        // step 2: prepare notification entity, mark it unread and save it
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(dto.message());
        notification.setType(dto.type());
        notification.setRead(false); // Lombok: boolean isRead -> setRead()

        notification = notificationRepository.save(notification);
        return new CreatedRespDto(notification.getId(), "Notification created");
    }

    public List<NotificationRespDto> getByUserId(Long userId, String loggedInUsername) {
        // step 1: fetch the user, throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // step 2: a user can read only his own notifications
        if (!user.getUsername().equals(loggedInUsername))
            throw new OwnershipException("You can read only your own notifications");

        return notificationRepository.getByUserId(userId);
    }

    public void markRead(Long id, String loggedInUsername) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        checkOwner(notification, loggedInUsername);

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteById(Long id, String loggedInUsername) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        checkOwner(notification, loggedInUsername);

        notificationRepository.delete(notification);
    }

    private void checkOwner(Notification notification, String loggedInUsername) {
        if (!notification.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("This notification belongs to another user");
    }
}