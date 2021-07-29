package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IndividualTrainingsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private IndividualTrainingsRepository individualTrainingsRepository;

    public boolean isIndividualTrainingExist(String trainingId){
        return individualTrainingsRepository.existsIndividualTrainingsById(trainingId);
    }

    public boolean isIndividualTrainingExistAndAccepted(String trainingId){
        return individualTrainingsRepository.existsIndividualTrainingsByIdAndAcceptedEquals(trainingId, true);
    }

    public boolean isIndividualTrainingExistAndDeclined(String trainingId){
        return individualTrainingsRepository.existsIndividualTrainingsByIdAndDeclinedEquals(trainingId, true);
    }

    public boolean isIndividualTrainingExistAndRequestedByClient(String trainingId, String clientId){
        return individualTrainingsRepository.existsIndividualTrainingsByIdAndClientIdEquals(trainingId, clientId);
    }

    public List<IndividualTrainings> getIndividualTrainings(){
        return individualTrainingsRepository.findAll();
    }

    public IndividualTrainings getIndividualTrainingById(String trainingId){
        return individualTrainingsRepository.findIndividualTrainingsById(trainingId);
    }

    public List<IndividualTrainings> getMyAllIndividualTrainings(String clientId){
        return individualTrainingsRepository.findIndividualTrainingsByClientIdEquals(clientId);
    }

    public List<IndividualTrainings> getAcceptedIndividualTrainings(){
        return individualTrainingsRepository.findAllByAccepted(true);
    }

    public IndividualTrainings createIndividualTrainingRequest(IndividualTrainingRequest individualTrainingsRequestModel,
                                                               String clientId) throws InvalidHourException {
        IndividualTrainings response = individualTrainingsRepository.insert(new IndividualTrainings(
                clientId,
                individualTrainingsRequestModel.getTrainerId(),
                individualTrainingsRequestModel.getDate(),
                individualTrainingsRequestModel.getStartTime(),
                individualTrainingsRequestModel.getEndTime(),
                -1,
                individualTrainingsRequestModel.getRemarks(),
                false,
                false
        ));
        return response;
    }

    public IndividualTrainings acceptIndividualTrainingRequest(String trainingId,
                                                               IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel){

        IndividualTrainings individualTrainings = individualTrainingsRepository.findIndividualTrainingsById(trainingId);
        individualTrainings.setAccepted(true);
        individualTrainings.setHallNo(individualTrainingsAcceptModel.getHallNo());
        IndividualTrainings response = individualTrainingsRepository.save(individualTrainings);
        return response;
    }

    public IndividualTrainings declineIndividualTrainingRequest(String trainingId){
        IndividualTrainings individualTrainings = individualTrainingsRepository.findIndividualTrainingsById(trainingId);
        individualTrainings.setDeclined(true);
        IndividualTrainings response = individualTrainingsRepository.save(individualTrainings);
        return response;
    }

    public IndividualTrainings cancelIndividualTrainingRequest(String trainingId){
        IndividualTrainings individualTrainings = individualTrainingsRepository.findIndividualTrainingsById(trainingId);
        individualTrainingsRepository.deleteIndividualTrainingsById(trainingId);
        return individualTrainings;
    }

}
