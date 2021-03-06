package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.component.CollisionValidatorComponent;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsDAO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.StartEndDateNotSameDayException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.NotificationService;
import com.healthy.gym.trainings.utils.CollisionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapGroupTrainingsDocumentToDTO;

@Service
public class ManagerGroupTrainingServiceImpl implements ManagerGroupTrainingService {

    private final CollisionValidatorComponent collisionValidatorComponent;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final LocationDAO locationDAO;
    private final UserDAO userDAO;
    private final Clock clock;
    private final GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder;
    private final NotificationService notificationService;

    @Autowired
    public ManagerGroupTrainingServiceImpl(
            CollisionValidatorComponent collisionValidatorComponent,
            GroupTrainingsDAO groupTrainingsDAO,
            TrainingTypeDAO trainingTypeDAO,
            LocationDAO locationDAO,
            UserDAO userDAO,
            Clock clock,
            GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder,
            NotificationService notificationService
    ) {
        this.collisionValidatorComponent = collisionValidatorComponent;
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.locationDAO = locationDAO;
        this.userDAO = userDAO;
        this.clock = clock;
        this.groupTrainingDocumentUpdateBuilder = groupTrainingDocumentUpdateBuilder;
        this.notificationService = notificationService;
    }

    @Override
    public GroupTrainingDTO createGroupTraining(
            final ManagerGroupTrainingRequest createGroupTrainingRequest
    ) throws StartDateAfterEndDateException,
            TrainerNotFoundException,
            LocationNotFoundException,
            TrainingTypeNotFoundException,
            LocationOccupiedException,
            TrainerOccupiedException,
            PastDateException,
            StartEndDateNotSameDayException {

        TrainingTypeDocument trainingType = getTrainingTypeDocument(createGroupTrainingRequest);
        List<UserDocument> trainers = getListOfTrainersUserDocument(createGroupTrainingRequest);
        LocationDocument location = getLocationDocument(createGroupTrainingRequest);
        LocalDateTime startDate = getStartDate(createGroupTrainingRequest);
        LocalDateTime endDate = getEndDate(createGroupTrainingRequest);
        int limit = createGroupTrainingRequest.getLimit();

        GroupTrainingDocument groupTrainingToCreate = new GroupTrainingDocument(
                UUID.randomUUID().toString(),
                trainingType,
                trainers,
                startDate,
                endDate,
                location,
                limit,
                new ArrayList<>(),
                new ArrayList<>()
        );

        validateStartDateTime(groupTrainingToCreate);
        validateStartEndDateIfTheSameDay(startDate, endDate);
        checkIfStartDateTimeIsBeforeEndDateTime(groupTrainingToCreate);
        validateIfLocationOrTrainerIsOccupied(groupTrainingToCreate, null);

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTrainingToCreate);
        return mapGroupTrainingsDocumentToDTO(groupTrainingSaved);
    }

    private TrainingTypeDocument getTrainingTypeDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws TrainingTypeNotFoundException {
        String trainingTypeId = groupTrainingRequest.getTrainingTypeId();
        TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
        if (trainingType == null) throw new TrainingTypeNotFoundException();
        return trainingType;
    }

    private List<UserDocument> getListOfTrainersUserDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws TrainerNotFoundException {

        List<String> trainerIds = groupTrainingRequest.getTrainerIds();
        List<UserDocument> trainers = new ArrayList<>();
        for (String trainerId : trainerIds) {
            UserDocument trainer = userDAO.findByUserId(trainerId);
            if (trainer == null || !trainer.getGymRoles().contains(GymRole.TRAINER))
                throw new TrainerNotFoundException();
            trainers.add(trainer);
        }
        return trainers;
    }

    private LocationDocument getLocationDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws LocationNotFoundException {
        String locationId = groupTrainingRequest.getLocationId();
        LocationDocument location = locationDAO.findByLocationId(locationId);
        if (location == null) throw new LocationNotFoundException();
        return location;
    }

    private LocalDateTime getStartDate(ManagerGroupTrainingRequest groupTrainingRequest) {
        String startDate = groupTrainingRequest.getStartDate();
        return parseDateTime(startDate);
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private LocalDateTime getEndDate(ManagerGroupTrainingRequest groupTrainingRequest) {
        String endDate = groupTrainingRequest.getEndDate();
        return parseDateTime(endDate);
    }

    private void validateStartDateTime(GroupTrainingDocument groupTraining) throws PastDateException {
        LocalDateTime startDate = groupTraining.getStartDate();
        if (startDate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();
    }

    private void validateStartEndDateIfTheSameDay(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws StartEndDateNotSameDayException {

        var startDay = startDateTime.toLocalDate();
        var endDay = endDateTime.toLocalDate();
        if (!startDay.equals(endDay)) throw new StartEndDateNotSameDayException();
    }

    private void checkIfStartDateTimeIsBeforeEndDateTime(GroupTrainingDocument groupTraining)
            throws StartDateAfterEndDateException {
        LocalDateTime startDate = groupTraining.getStartDate();
        LocalDateTime endDate = groupTraining.getEndDate();
        if (endDate.isBefore(startDate)) throw new StartDateAfterEndDateException();
    }

    private void validateIfLocationOrTrainerIsOccupied(GroupTrainingDocument groupTrainingToCreate, String trainingId)
            throws LocationOccupiedException, TrainerOccupiedException {

        LocalDateTime startDateTime = groupTrainingToCreate.getStartDate();
        LocalDateTime endDateTime = groupTrainingToCreate.getEndDate();

        CollisionValidator validator;
        if (trainingId == null) {
            validator = collisionValidatorComponent
                    .getCollisionValidator(startDateTime, endDateTime);
        } else {
            validator = collisionValidatorComponent
                    .getCollisionValidator(startDateTime, endDateTime, trainingId);
        }

        LocationDocument location = groupTrainingToCreate.getLocation();
        boolean isLocationOccupied = validator.isLocationOccupied(location);
        if (isLocationOccupied) throw new LocationOccupiedException();

        List<UserDocument> trainers = groupTrainingToCreate.getTrainers();
        boolean isTrainerOccupied = validator.isTrainerOccupied(trainers);
        if (isTrainerOccupied) throw new TrainerOccupiedException();

    }

    @Override
    public GroupTrainingDTO updateGroupTraining(
            final String trainingId,
            final ManagerGroupTrainingRequest groupTrainingRequest
    ) throws LocationNotFoundException,
            LocationOccupiedException,
            NotExistingGroupTrainingException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerNotFoundException,
            TrainerOccupiedException,
            TrainingTypeNotFoundException,
            StartEndDateNotSameDayException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        GroupTrainingDocument groupTrainingUpdated = groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(groupTraining)
                .setGroupTrainingRequest(groupTrainingRequest)
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update();

        validateStartDateTime(groupTrainingUpdated);
        validateStartEndDateIfTheSameDay(groupTrainingUpdated.getStartDate(), groupTrainingUpdated.getEndDate());
        checkIfStartDateTimeIsBeforeEndDateTime(groupTrainingUpdated);
        validateIfLocationOrTrainerIsOccupied(groupTrainingUpdated, trainingId);

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTrainingUpdated);

        sendNotificationsWhenUpdated(groupTrainingSaved, groupTrainingRequest.shouldSendEmails());

        return mapGroupTrainingsDocumentToDTO(groupTrainingSaved);
    }

    private void sendNotificationsWhenUpdated(GroupTrainingDocument groupTraining, boolean shouldSendEmails) {
        if (isPastDate(groupTraining)) return;

        List<UserDocument> users = getAllUsersInTraining(groupTraining);
        notificationService.sendNotificationsAndEmailsWhenUpdatingGroupTraining(
                groupTraining.getTraining().getName(),
                groupTraining.getStartDate(),
                users,
                shouldSendEmails
        );
    }

    private boolean isPastDate(GroupTrainingDocument groupTraining) {
        LocalDateTime startDate = groupTraining.getStartDate();
        return startDate.isAfter(LocalDateTime.now(clock));
    }

    private List<UserDocument> getAllUsersInTraining(GroupTrainingDocument groupTraining) {
        List<UserDocument> allGroupTrainingUsers = new ArrayList<>();
        allGroupTrainingUsers.addAll(groupTraining.getTrainers());
        allGroupTrainingUsers.addAll(groupTraining.getBasicList());
        allGroupTrainingUsers.addAll(groupTraining.getReserveList());
        return allGroupTrainingUsers;
    }

    @Override
    public GroupTrainingDTO removeGroupTraining(String trainingId) throws NotExistingGroupTrainingException {

        GroupTrainingDocument groupTrainingToDelete = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTrainingToDelete == null) throw new NotExistingGroupTrainingException();
        groupTrainingsDAO.delete(groupTrainingToDelete);

        sendNotificationsWhenRemove(groupTrainingToDelete);

        return mapGroupTrainingsDocumentToDTO(groupTrainingToDelete);
    }

    private void sendNotificationsWhenRemove(GroupTrainingDocument groupTraining) {
        if (isPastDate(groupTraining)) return;
        //todo add notification when group training removed
    }
}
