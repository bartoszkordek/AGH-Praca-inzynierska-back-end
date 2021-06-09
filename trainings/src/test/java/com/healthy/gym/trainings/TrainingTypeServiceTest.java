package com.healthy.gym.trainings;

import com.healthy.gym.trainings.db.TrainingTypeRepository;
import com.healthy.gym.trainings.entity.TrainingType;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.mock.TrainingTypeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class TrainingTypeServiceTest {


    private final String validTrainingTypeId = "111111111111111111111111";
    private final String invalidTrainingTypeId = "999999999999999999999999";

    private TrainingType validTrainingType;

    public TrainingTypeServiceTest(){

    }


    @TestConfiguration
    static class TrainingTypesServiceImplTestContextConfiguration {

        @Bean
        public TrainingTypeServiceImpl trainingTypeService() {
            return new TrainingTypeServiceImpl(null);
        }

    }

    @Autowired
    TrainingTypeServiceImpl trainingTypeService;

    @MockBean
    private TrainingTypeRepository trainingTypeRepository;

    @Before
    public void setUp() {


        validTrainingType = new TrainingType("Valid Training Name", "Sample Description", null);
        validTrainingType.setId(validTrainingTypeId);
        when(trainingTypeRepository.existsTrainingTypeById(validTrainingTypeId))
                .thenReturn(true);
        when(trainingTypeRepository.findTrainingTypeById(validTrainingTypeId))
                .thenReturn(validTrainingType);
    }

    @Test
    public void shouldReturnTrainingTypeById_whenValidRequest() throws NotExistingTrainingType {
        assertThat(trainingTypeService.getTrainingTypeById(validTrainingTypeId))
                .isEqualTo(validTrainingType);
    }

    @Test(expected = NotExistingTrainingType.class)
    public void shouldNotReturnTrainingTypeById_whenTrainingIdNotExists() throws NotExistingTrainingType {
        trainingTypeService.getTrainingTypeById(invalidTrainingTypeId);
    }



}
