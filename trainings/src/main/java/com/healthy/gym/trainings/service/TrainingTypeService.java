package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.dto.TrainingTypeDTO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrainingTypeService {

    TrainingTypeDTO createTrainingType(TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile)
            throws DuplicatedTrainingTypeException;

    List<TrainingTypeDTO> getAllTrainingTypes() throws TrainingTypeNotFoundException;

    TrainingTypeDTO getTrainingTypeById(String trainingTypeId) throws TrainingTypeNotFoundException;

    TrainingTypeDTO updateTrainingTypeById(
            String trainingId, TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile
    ) throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException;

    TrainingTypeDTO removeTrainingTypeById(String trainingName) throws TrainingTypeNotFoundException;
}
