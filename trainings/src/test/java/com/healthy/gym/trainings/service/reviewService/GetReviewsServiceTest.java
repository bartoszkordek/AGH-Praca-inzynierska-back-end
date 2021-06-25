package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.InvalidUserIdException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GetReviewsServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldReturnAllReviews_whenValidRequest() throws ParseException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(reviewRepository.findByDateBetween(startDateMinusOneDay,
                endDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(startDate, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDay,
                        endDatePlusOneDay, pageable));


        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetween(startDateMinusOneDay,
                defaultEndDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(startDate, null, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                endDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(null, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(null, null, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, pageable));
    }

    @Test
    public void shouldReturnAllReviewsInTimeFrame_whenValidRequest() throws ParseException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviewsAll = new ArrayList<>();
        List<GroupTrainingReviewResponse> reviewsBeforeJuly2021 = new ArrayList<>();
        List<GroupTrainingReviewResponse> reviewsAfterJuly2021 = new ArrayList<>();

        String reviewIdRev1 = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingNameRev1 = "TestTrainingName1";
        String clientIdRev1 = "Client123";
        String dateRev1 = "2021-01-01";
        int starsRev1 = 5;
        String textRev1 = "Very good training!";
        GroupTrainingReviewResponse review1 = new GroupTrainingReviewResponse(
                reviewIdRev1,
                trainingNameRev1,
                clientIdRev1,
                dateRev1,
                starsRev1,
                textRev1);
        reviewsAll.add(review1);
        reviewsBeforeJuly2021.add(review1);

        String reviewIdRev2 = "852ed953-e37f-435a-bd1e-9fb2a327c4d6";
        String trainingNameRev2 = "TestTrainingName2";
        String clientIdRev2 = "Client123";
        String dateRev2 = "2021-08-01";
        int starsRev2 = 4;
        String textRev2 = "Good training!";
        GroupTrainingReviewResponse review2 = new GroupTrainingReviewResponse(
                reviewIdRev2,
                trainingNameRev2,
                clientIdRev2,
                dateRev2,
                starsRev2,
                textRev2);
        reviewsAll.add(review2);
        reviewsAfterJuly2021.add(review2);

        Page<GroupTrainingReviewResponse> reviewsBeforeJuly2021InPages = new PageImpl<>(reviewsBeforeJuly2021);
        Page<GroupTrainingReviewResponse> reviewsAfterJuly2021InPages = new PageImpl<>(reviewsAfterJuly2021);

        //populated both start and end date before July 2021 (between 2021-01-01 and 2021-07-01)
        String beforeJuly2021PeriodStartDate = "2021-01-01";
        String beforeJuly2021PeriodEndDate = "2021-07-31";

        //when
        String beforeJuly2021PeriodStartDateMinusOneDay = "2020-12-31";
        String beforeJuly2021PeriodEndDatePlusOneDay = "2021-08-01";
        when(reviewRepository.findByDateBetween(beforeJuly2021PeriodStartDateMinusOneDay,
                beforeJuly2021PeriodEndDatePlusOneDay, pageable))
                .thenReturn(reviewsBeforeJuly2021InPages);
        //then
        assertThat(reviewService.getAllReviews(beforeJuly2021PeriodStartDate, beforeJuly2021PeriodEndDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(beforeJuly2021PeriodStartDateMinusOneDay,
                        beforeJuly2021PeriodEndDatePlusOneDay, pageable));


        //populated both start and end date after July 2021 (between 2021-08-01 and 2021-31-01)
        String afterJuly2021PeriodStartDate = "2021-08-01";
        String afterJuly2021PeriodEndDate = "2021-12-31";

        //when
        String afterJuly2021PeriodStartDateMinusOneDay = "2021-07-31";
        String afterJuly2021PeriodEndDatePlusOneDay = "2022-01-01";
        when(reviewRepository.findByDateBetween(afterJuly2021PeriodStartDateMinusOneDay,
                afterJuly2021PeriodEndDatePlusOneDay, pageable))
                .thenReturn(reviewsBeforeJuly2021InPages);

        //then
        assertThat(reviewService.getAllReviews(afterJuly2021PeriodStartDate, afterJuly2021PeriodEndDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(afterJuly2021PeriodStartDateMinusOneDay,
                        afterJuly2021PeriodEndDatePlusOneDay, pageable));
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviews_whenStartDateAfterEndDate() throws ParseException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String startDate = "2021-01-02";
        String endDate = "2021-01-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        //then
        assertThat(reviewService.getAllReviews(startDate, endDate, pageable));
    }

    @Test
    public void shouldReturnAllReviewsByUserId_whenValidRequest() throws ParseException, InvalidUserIdException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                endDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviewsByUserId(startDate, endDate, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                        endDatePlusOneDay, validClientId, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                endDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviewsByUserId(null, endDate, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, validClientId, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByUserId(startDate, null, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, validClientId, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByUserId(null, null, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, validClientId, pageable));
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviewsByUserId_whenStartDateAfterEndDate() throws ParseException, StartDateAfterEndDateException, InvalidUserIdException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        String startDate = "2021-31-12";
        String endDate = "2021-01-01";

        //then
        reviewService.getAllReviewsByUserId(startDate, endDate, validClientId, pageable);
    }

    @Test(expected = InvalidUserIdException.class)
    public void shouldNotReturnAllReviewsByUserId_whenEmptyUserId() throws ParseException, InvalidUserIdException, StartDateAfterEndDateException, InvalidUserIdException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        reviewService.getAllReviewsByUserId(null, null, null, pageable);
    }

    @Test
    public void shouldReturnAllReviewsByTrainingTypeId_whenValidRequest() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";

        when(trainingTypeRepository.existsByTrainingTypeId(trainingName)).thenReturn(true);
        when(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                        endDatePlusOneDay, trainingName, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(null, endDate, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, trainingName, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(startDate, null, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingName, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(null, null, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingName, pageable));
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviewsByTrainingTypeId_whenStartDateAfterEndDate() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(reviewRepository.existsById(trainingName)).thenReturn(true);
        when(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);

        String startDate = "2021-31-12";
        String endDate = "2021-01-01";

        //then
        reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingName, pageable);
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotReturnAllReviewsByTrainingTypeId_whenEmptyTrainingName() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay, defaultEndDatePlusOneDay,
                trainingName, pageable))
                .thenReturn(reviewsInPages);

        //then
        reviewService.getAllReviewsByTrainingTypeId(defaultStartDateMinusOneDay, defaultEndDatePlusOneDay,
                null, pageable);

    }


    @Test
    public void shouldReturnAllPublicReviewsByTrainingTypeId_whenValidRequest() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewPublicResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewPublicResponse review = new GroupTrainingReviewPublicResponse(
                reviewId,
                trainingName,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewPublicResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(trainingTypeRepository.existsByTrainingTypeId(trainingName)).thenReturn(true);
        when(reviewRepository.getAllByDateBetweenAndTrainingName(startDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(startDate, endDate, trainingName, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingName(startDateMinusOneDay,
                        endDatePlusOneDay, trainingName, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.getAllByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(null, endDate, trainingName, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, trainingName, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.getAllByDateBetweenAndTrainingName(startDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(startDate, null, trainingName, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingName(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingName, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.getAllByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(null, null, trainingName, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingName, pageable));
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotReturnAllPublicReviewsByTrainingTypeId_whenEmptyTrainingName() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewPublicResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewPublicResponse review = new GroupTrainingReviewPublicResponse(
                reviewId,
                trainingName,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewPublicResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(reviewRepository.getAllByDateBetweenAndTrainingName(startDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);

        //then
        reviewService.getAllReviewsByTrainingTypeIdPublic(startDate, endDate, trainingName, pageable);
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllPublicReviewsByTrainingTypeId_whenStartDateAfterEndDate() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewPublicResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewPublicResponse review = new GroupTrainingReviewPublicResponse(
                reviewId,
                trainingName,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewPublicResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(reviewRepository.existsById(trainingName)).thenReturn(true);
        when(reviewRepository.getAllByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);

        String startDate = "2021-31-12";
        String endDate = "2021-01-01";

        //then
        reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingName, pageable);
    }


}