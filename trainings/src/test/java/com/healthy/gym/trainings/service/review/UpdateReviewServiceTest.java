package com.healthy.gym.trainings.service.review;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UpdateReviewServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldUpdateReview_whenValidRequestAndReviewIdAndClientId() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "Good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);


        //when
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(true);

        //then
        assertThat(reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId))
                .isEqualTo(response);
    }

    @Test
    public void shouldUpdateOnlyStarsReview_whenEmptyText() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textBeforeUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textBeforeUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(true);

        assertThat(reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId))
                .isEqualTo(response);
    }

    @Test(expected = NotExistingGroupTrainingReviewException.class)
    public void shouldNotUpdateReview_whenInvalidReviewId() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "Good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(false);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(false);

        //then
        reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId);
    }

    @Test(expected = NotAuthorizedClientException.class)
    public void shouldNotUpdateReview_whenClientIsNotOwnerOfReview() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "Good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(false);

        //then
        reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId);
    }

    @Test(expected = StarsOutOfRangeException.class)
    public void shouldNotUpdateReview_whenStarsLessThan1() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 0;
        String textAfterUpdate = "Zero!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(true);

        //then
        reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId);
    }

    @Test(expected = StarsOutOfRangeException.class)
    public void shouldNotUpdateReview_whenStarsGreaterThan5() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 6;
        String textAfterUpdate = "Ultra good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingTypeId,
                clientId, date, starsAfterUpdate, textAfterUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(true);

        //then
        reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId);
    }
}
