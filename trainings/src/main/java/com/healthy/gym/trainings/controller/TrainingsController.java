package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TrainingsController {

    TrainingsService trainingsService;

    public TrainingsController(TrainingsService trainingsService){
        this.trainingsService = trainingsService;
    }

    @GetMapping("/status")
    public String status(){
        return "OK";
    }

    @GetMapping("/test/document/first")
    public String getFirstTestDocument(){
        return trainingsService.getFirstTestDocument();
    }

    @GetMapping("/group")
    public List<GroupTrainings> getGroupTrainings() {
        return trainingsService.getGroupTrainings();
    }

    @GetMapping("/group/{trainingId}")
    public GroupTrainings getGroupTrainingById(@PathVariable("trainingId") final String trainingId) throws NotExistingGroupTrainingException {
        return trainingsService.getGroupTrainingById(trainingId);
    }
}
