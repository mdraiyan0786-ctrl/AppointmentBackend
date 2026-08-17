package com.appointment.service;

import com.appointment.entity.Notification;
import com.appointment.entity.User;
import com.appointment.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // Create notification
    public Notification createNotification(
            User user,
            String message) {

        Notification notification = new Notification();

        notification.setMessage(message);
        notification.setUser(user);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    // Get logged-in user's notifications
    public List<Notification> getMyNotifications(Long userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Get unread notification count
    public long getUnreadCount(Long userId) {

        return notificationRepository
                .countByUserIdAndIsReadFalse(userId);
    }

    // Mark notification as read
    public void markAsRead(Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    // Temporary test notification
    public Notification createTestNotification(User user) {

        return createNotification(
                user,
                "This is a test notification."
        );
    }
}