package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.UserDocument;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {

    void sendNotificationsAndEmailsWhenUpdatingGroupTraining(
            String trainingName,
            LocalDateTime startDateTime,
            List<UserDocument> userDocumentList,
            boolean shouldSendEmails
    );

    void sendNotificationsAndEmailsWhenRemovingGroupTraining(
            String trainingName,
            LocalDateTime startDateTime,
            List<UserDocument> userDocumentList,
            boolean shouldSendEmails
    );

    void sendNotificationWhenCreateIndividualTrainingRequest(
            UserDocument toTrainer,
            UserDocument fromUser,
            LocalDateTime startDateTime
    );

    void sendNotificationWhenCancelIndividualTrainingRequest(
            UserDocument toTrainer,
            UserDocument fromUser,
            LocalDateTime startDateTime
    );
}
