package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.repository.TrainingTypeRepository;
import com.healthy.gym.trainings.data.document.TrainingType;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.TrainingTypeManagerViewModel;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public List<TrainingTypeManagerViewModel> getAllTrainingTypesManagerView() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypeManagerViewModel> trainingTypeManagerViewModels = new ArrayList<>();
        for (TrainingType trainingType : trainingTypes) {
            TrainingTypeManagerViewModel trainingTypeManagerViewModel = new TrainingTypeManagerViewModel(
                    trainingType.getId(),
                    trainingType.getTrainingName(),
                    trainingType.getDescription(),
                    trainingType.getAvatar()
            );
            trainingTypeManagerViewModels.add(trainingTypeManagerViewModel);
        }

        return trainingTypeManagerViewModels;
    }

    public List<TrainingTypePublicViewModel> getAllTrainingTypesPublicView() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypePublicViewModel> trainingTypePublicViewModels = new ArrayList<>();
        for (TrainingType trainingType : trainingTypes) {
            TrainingTypePublicViewModel trainingTypePublicViewModel = new TrainingTypePublicViewModel(
                    trainingType.getTrainingName(),
                    trainingType.getDescription(),
                    trainingType.getAvatar()
            );
            trainingTypePublicViewModels.add(trainingTypePublicViewModel);
        }

        return trainingTypePublicViewModels;
    }

    public TrainingType getTrainingTypeById(String trainingTypeId) throws NotExistingTrainingType {
        if (!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)) {
            throw new NotExistingTrainingType("Training type of id: " + trainingTypeId + " not exist.");
        }
        return trainingTypeRepository.findTrainingTypeById(trainingTypeId);
    }

    public TrainingType createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar) throws DuplicatedTrainingTypes {
        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        if (trainingTypeRepository.existsByTrainingName(trainingName)) {
            throw new DuplicatedTrainingTypes("Training type of name: " + trainingName + " already exists.");
        }

        TrainingType response = trainingTypeRepository.insert(new TrainingType(trainingName, description, avatar));
        return response;
    }

    public TrainingType removeTrainingTypeByName(String trainingName) throws NotExistingTrainingType {
        if (!trainingTypeRepository.existsByTrainingName(trainingName)) {
            throw new NotExistingTrainingType("Training type of name: " + trainingName + " not exist.");
        }

        TrainingType trainingTypeToRemove = trainingTypeRepository.findTrainingTypeByTrainingName(trainingName);
        trainingTypeRepository.removeTrainingTypeByTrainingName(trainingName);

        return trainingTypeToRemove;
    }

    public TrainingType updateTrainingTypeById(String trainingTypeId, TrainingTypeModel trainingTypeModel, byte[] avatar) throws NotExistingTrainingType, DuplicatedTrainingTypes {
        if (!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)) {
            throw new NotExistingTrainingType("Training type of id: " + trainingTypeId + " not exist.");
        }

        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        TrainingType trainingType = trainingTypeRepository.findTrainingTypeById(trainingTypeId);
        trainingType.setTrainingName(trainingName);
        trainingType.setDescription(description);
        trainingType.setAvatar(avatar);

        return trainingTypeRepository.save(trainingType);
    }
}
