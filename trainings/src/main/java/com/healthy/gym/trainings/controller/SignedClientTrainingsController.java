package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SignedClientTrainingsController {

    TrainingsService trainingsService;

    public SignedClientTrainingsController(TrainingsService trainingsService){
        this.trainingsService = trainingsService;
    }

    @PostMapping("/group/{trainingId}/enroll")
    public void enrollToGroupTraining(@PathVariable("trainingId") final String trainingId,
                                      @RequestParam(required = true) final String clientId) throws RestException {
        try{
            trainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }

    @PostMapping("/group/{trainingId}/reservelist/add")
    public void addToReserveList(@PathVariable("trainingId") final String trainingId,
                                 @RequestParam(required = true) final String clientId) throws RestException {
        try{
            trainingsService.addToReserveList(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/{trainingId}/enroll")
    public void removeGroupTrainingEnrollment(@PathVariable("trainingId") final String trainingId,
                                              @RequestParam(required = true) final String clientId) throws RestException {
        try{
            trainingsService.removeGroupTrainingEnrollment(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }
}
