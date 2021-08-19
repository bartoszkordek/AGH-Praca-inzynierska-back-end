package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.NotificationDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.NotificationDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.NoNotificationFoundException;
import com.healthy.gym.account.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private NotificationService notificationService;
    private String userId;

    @BeforeEach
    void setUp() {
        notificationDAO = mock(NotificationDAO.class);
        userDAO = mock(UserDAO.class);
        notificationService = new NotificationServiceImpl(notificationDAO, userDAO);

        userId = UUID.randomUUID().toString();
    }

    @Nested
    class GetRecentUserNotifications {
        @Test
        void shouldThrowUserNotFoundException() {
            when(userDAO.findByUserId(anyString())).thenReturn(null);

            assertThatThrownBy(
                    () -> notificationService.getRecentUserNotifications(userId, 0, 10)
            ).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowNoNotificationFoundException() {
            when(userDAO.findByUserId(anyString())).thenReturn(new UserDocument());
            when(notificationDAO.findAllByToEquals(any(), any()))
                    .thenReturn(Page.empty());

            assertThatThrownBy(
                    () -> notificationService.getRecentUserNotifications(userId, 0, 10)
            ).isInstanceOf(NoNotificationFoundException.class);
        }

        @Disabled("need to be fixed")
        @Test
        void shouldReturnProperList() {
            when(userDAO.findByUserId(anyString())).thenReturn(new UserDocument());
            var notifications= List.of(new NotificationDocument());
            when(notificationDAO.findAllByToEquals(any(), any()))
                    .thenReturn(new PageImpl<>(notifications));

            assertThatThrownBy(
                    () -> notificationService.getRecentUserNotifications(userId, 0, 10)
            ).isInstanceOf(NoNotificationFoundException.class);
        }
    }


}