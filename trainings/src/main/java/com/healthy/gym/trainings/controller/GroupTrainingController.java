package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.healthy.gym.trainings.model.GroupTrainingsPublicViewModel;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupTrainingController {

    private final GroupTrainingsService groupTrainingsService;

    @Autowired
    public GroupTrainingController(GroupTrainingsService groupTrainingsService) {
        this.groupTrainingsService = groupTrainingsService;
    }

    // TODO only manager
    @PostMapping
    public GroupTrainings createGroupTraining(
            @Valid @RequestBody GroupTrainingModel groupTrainingModel
    ) throws RestException {
        try {
            return groupTrainingsService.createGroupTraining(groupTrainingModel);
        } catch (TrainingCreationException | InvalidHourException | ParseException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping
    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsService.getGroupTrainings();
    }

    @GetMapping("/public")
    public List<GroupTrainingsPublicViewModel> getPublicGroupTrainings()
            throws InvalidHourException, InvalidDateException {
        return groupTrainingsService.getPublicGroupTrainings();
    }

    @GetMapping("/{trainingId}")
    public GroupTrainings getGroupTrainingById(
            @PathVariable("trainingId") final String trainingId
    ) throws NotExistingGroupTrainingException {
        return groupTrainingsService.getGroupTrainingById(trainingId);
    }

    // TODO only manager
    @PutMapping("/{trainingId}")
    public GroupTrainings updateGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody GroupTrainingModel groupTrainingModelRequest
    ) throws RestException {
        try {
            return groupTrainingsService.updateGroupTraining(trainingId, groupTrainingModelRequest);
        } catch (TrainingUpdateException | InvalidHourException | EmailSendingException | ParseException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO only manager
    @DeleteMapping("/{trainingId}")
    public GroupTrainings removeGroupTraining(@PathVariable("trainingId") final String trainingId)
            throws RestException {
        try {
            return groupTrainingsService.removeGroupTraining(trainingId);
        } catch (TrainingRemovalException | EmailSendingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO only logged in users and ADMIN, dodać po ID
    @GetMapping("/trainings/{userId}")
    public List<GroupTrainings> getAllGroupTrainingsByUserId(@PathVariable final String userId) {
        return groupTrainingsService.getMyAllTrainings(userId);
    }

    //TODO only with USER ROLE
    @PostMapping("/{trainingId}/enroll")
    public void enrollToGroupTraining(@PathVariable("trainingId") final String trainingId,
                                      @RequestParam(required = true) final String clientId) throws RestException {
        try {
            groupTrainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }

    @PostMapping("/{trainingId}/reservelist/add")
    public void addToReserveList(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam(required = true) final String clientId
    ) throws RestException {
        try {
            groupTrainingsService.addToReserveList(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    //TODO aktualizacja listy podstawowej i listy rezerwowej
    @DeleteMapping("/{trainingId}/enroll")
    public void removeGroupTrainingEnrollment(@PathVariable("trainingId") final String trainingId,
                                              @RequestParam(required = true) final String clientId) throws RestException {
        try {
            groupTrainingsService.removeGroupTrainingEnrollment(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }
}
