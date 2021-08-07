package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.data.repository.IndividualTrainingsRepository;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeIndividualTrainingServiceImpl implements EmployeeIndividualTrainingService {

    private final IndividualTrainingsRepository individualTrainingsRepository;

    @Autowired
    public EmployeeIndividualTrainingServiceImpl(IndividualTrainingsRepository individualTrainingsRepository) {
        this.individualTrainingsRepository = individualTrainingsRepository;
    }

    @Override
    public List<IndividualTrainingDTO> getIndividualTrainings(
            final String startDate,
            final String endDate,
            final int page,
            final int size
    ) throws StartDateAfterEndDateException, NoIndividualTrainingFoundException {
        //return individualTrainingsRepository.findAll();
        return List.of();
    }

    @Override
    public IndividualTrainingDTO getIndividualTrainingById(
            final String trainingId
    ) throws NotExistingIndividualTrainingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);
        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
        //return individualTraining;
        return null;
    }

    @Override
    public List<IndividualTrainingDTO> getAllAcceptedIndividualTrainings(
            final String startDate,
            final String endDate,
            final int page,
            final int size
    ) throws StartDateAfterEndDateException, NoIndividualTrainingFoundException {
        //return individualTrainingsRepository.findAllByAccepted(true);
        return List.of();
    }
}