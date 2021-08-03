package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.EmailSendingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.exception.training.TrainingRemovalException;
import com.healthy.gym.trainings.exception.training.TrainingUpdateException;
import com.healthy.gym.trainings.model.request.CreateGroupTrainingRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingsResponse;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;

import java.text.ParseException;

public interface ManagerGroupTrainingService {

    GroupTrainingDTO createGroupTraining(CreateGroupTrainingRequest createGroupTrainingRequest)
            throws StartDateAfterEndDateException,
            TrainerNotFoundException,
            LocationNotFoundException,
            TrainingTypeNotFoundException,
            LocationOccupiedException,
            TrainerOccupiedException, PastDateException;


    GroupTrainingDTO updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest)
            throws TrainingUpdateException,
            EmailSendingException,
            InvalidHourException,
            ParseException,
            InvalidDateException;

    GroupTrainingDTO removeGroupTraining(String trainingId)
            throws TrainingRemovalException,
            EmailSendingException,
            InvalidDateException,
            InvalidHourException,
            NotExistingGroupTrainingException;
}
