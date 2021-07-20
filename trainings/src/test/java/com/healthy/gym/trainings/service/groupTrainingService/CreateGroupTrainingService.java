package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.TrainingCreationException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.service.GroupTrainingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CreateGroupTrainingService {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldCreateGroupTraining_whenValidRequest() throws InvalidDateException, InvalidHourException, TrainingCreationException, ParseException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);
        GroupTrainingResponse groupTrainingResponse = new GroupTrainingResponse(trainingId, trainingName, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);

        //when
        when(groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        assertThat(groupTrainingService.createGroupTraining(groupTrainingRequest)).isEqualTo(groupTrainingResponse);
    }

    @Test(expected = TrainingCreationException.class)
    public void shouldNotCreateGroupTraining_whenOverlappingEvents() throws InvalidDateException, InvalidHourException, TrainingCreationException, ParseException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);
        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        //when
        when(groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(false);
        when(groupTrainingsDbRepository.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Test(expected = TrainingCreationException.class)
    public void shouldNotCreateGroupTraining_whenRetroDate() throws InvalidDateException, InvalidHourException, ParseException, TrainingCreationException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "1900-01-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        //when
        when(groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Test(expected = InvalidDateException.class)
    public void shouldNotCreateGroupTraining_whenInvalidDate() throws InvalidDateException, InvalidHourException, ParseException, TrainingCreationException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "1900-13-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        //when
        when(groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Test(expected = InvalidHourException.class)
    public void shouldNotCreateGroupTraining_whenInvalidHour() throws InvalidDateException, InvalidHourException, ParseException, TrainingCreationException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "25:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        //when
        when(groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Test(expected = TrainingCreationException.class)
    public void shouldNotCreateGroupTraining_whenStartDateAfterEndDate() throws InvalidDateException, InvalidHourException, ParseException, TrainingCreationException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTime = "19:00";
        String endTime = "18:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        //when
        when(groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        groupTrainingService.createGroupTraining(groupTrainingRequest);
    }
}
