package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.utils.DateFormatter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.getBasicList;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.getReserveList;

@Repository
public class GroupTrainingsDbRepositoryImpl implements GroupTrainingsDbRepository {

    private static final String GROUP_TRAININGS_COLLECTION_NAME = "GroupTrainings";

    private final Pageable paging;
    private final Environment environment;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final ReviewDAO groupTrainingsReviewsRepository;

    @Autowired
    public GroupTrainingsDbRepositoryImpl(
            Environment environment,
            GroupTrainingsRepository groupTrainingsRepository,
            ReviewDAO groupTrainingsReviewsRepository
    ) {
        this.environment = environment;
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.groupTrainingsReviewsRepository = groupTrainingsReviewsRepository;
        this.paging = PageRequest.of(0, 1000000);
    }

    @Override
    public List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {

        var dates = new DateFormatter(startDate, endDate);
        String dayBeforeStartDate = dates.getFormattedDayDateBeforeStartDate();
        String dayAfterEndDate = dates.getFormattedDayDateAfterEndDate();

        List<GroupTrainings> groupTrainingsDbResponse = groupTrainingsRepository
                .findByDateBetween(dayBeforeStartDate, dayAfterEndDate);

        List<GroupTrainingResponse> result = new ArrayList<>();
        for (GroupTrainings training : groupTrainingsDbResponse) {

            GroupTrainingResponse groupTraining = new GroupTrainingResponse(
                    training.getTrainingId(),
                    training.getTrainingType().getName(),
                    null, //TODO fix training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    getRatingForGroupTrainings(training),
                    getBasicList(training),
                    getReserveList(training)
            );
            result.add(groupTraining);
        }
        return result;
    }

    private double getRatingForGroupTrainings(GroupTrainings groupTraining) {
        List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository
                .findByDateBetweenAndTrainingTypeId(
                        null,
                        null,
                        groupTraining.getTrainingType().getTrainingTypeId(),
                        paging
                ).getContent();

        double rating = 0.0;
        double sum = 0;
        int counter = 0;
        for (GroupTrainingReviewResponse review : groupTrainingsReviews) {
            sum += review.getStars();
            counter++;
        }
        if (counter != 0) rating = sum / counter;

        return rating;
    }


    @Override
    public GroupTrainingResponse getGroupTrainingById(String trainingId)
            throws InvalidHourException, InvalidDateException {

        GroupTrainings groupTrainingsDbResponse = groupTrainingsRepository.findFirstByTrainingId(trainingId);

        return new GroupTrainingResponse(
                groupTrainingsDbResponse.getTrainingId(),
                groupTrainingsDbResponse.getTrainingType().getName(),
                null, //TODO fix groupTrainingsDbResponse.getTrainerId(),
                groupTrainingsDbResponse.getDate(),
                groupTrainingsDbResponse.getStartTime(),
                groupTrainingsDbResponse.getEndTime(),
                groupTrainingsDbResponse.getHallNo(),
                groupTrainingsDbResponse.getLimit(),
                getRatingForGroupTrainings(groupTrainingsDbResponse),
                getBasicList(groupTrainingsDbResponse),
                getReserveList(groupTrainingsDbResponse)
        );
    }

    @Override
    public List<GroupTrainingResponse> getGroupTrainingsByTrainingTypeId(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException {

        List<GroupTrainings> groupTrainingsList =
                getGroupTrainingsByTrainingTypeIdAndDates(trainingTypeId, startDate, endDate);
        double rating = getRatingForGroupTrainingList(groupTrainingsList);

        List<GroupTrainingResponse> result = new ArrayList<>();
        for (GroupTrainings training : groupTrainingsList) {

            GroupTrainingResponse groupTraining = new GroupTrainingResponse(
                    training.getTrainingId(),
                    training.getTrainingType().getName(),
                    null, //TODO fix training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    rating,
                    getBasicList(training),
                    getReserveList(training)
            );
            result.add(groupTraining);
        }
        return result;
    }

    private List<GroupTrainings> getGroupTrainingsByTrainingTypeIdAndDates(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException {

        var dates = new DateFormatter(startDate, endDate);
        String dayBeforeStartDate = dates.getFormattedDayDateBeforeStartDate();
        String dayAfterEndDate = dates.getFormattedDayDateAfterEndDate();

        return groupTrainingsRepository
                .findAllByTrainingTypeIdAndDateBetween(
                        trainingTypeId,
                        dayBeforeStartDate,
                        dayAfterEndDate
                );
    }

    private double getRatingForGroupTrainingList(@NotNull List<GroupTrainings> groupTrainingsList) {
        double rating = 0.0;
        if (!groupTrainingsList.isEmpty()) {
            List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository
                    .findByDateBetweenAndTrainingTypeId(
                            null,
                            null,
                            groupTrainingsList.get(0).getTrainingType().getTrainingTypeId(),
                            paging
                    ).getContent();

            double sum = 0;
            int counter = 0;
            for (GroupTrainingReviewResponse review : groupTrainingsReviews) {
                sum += review.getStars();
                counter++;
            }
            if (counter != 0) rating = sum / counter;
        }
        return rating;
    }


    @Override
    public List<GroupTrainingPublicResponse> getGroupTrainingsPublicByTrainingTypeId(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException {

        List<GroupTrainings> groupTrainingsList =
                getGroupTrainingsByTrainingTypeIdAndDates(trainingTypeId, startDate, endDate);
        double rating = getRatingForGroupTrainingList(groupTrainingsList);

        List<GroupTrainingPublicResponse> result = new ArrayList<>();
        for (GroupTrainings training : groupTrainingsList) {
            GroupTrainingPublicResponse groupTraining = new GroupTrainingPublicResponse(
                    training.getTrainingId(),
                    training.getTrainingType().getName(),
                    null, //TODO fix training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    rating
            );
            result.add(groupTraining);
        }
        return result;
    }

    @Override
    public boolean isAbilityToGroupTrainingEnrollment(String trainingId) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        String timeNowFormatted = sdfTime.format(now);

        if (!groupTrainingsRepository.existsByTrainingId(trainingId)) return false;

        int participantsCount = groupTrainingsRepository
                .getFirstByTrainingId(trainingId)
                .getParticipants()
                .size();

        boolean isAbilityInTheFutureEvents = groupTrainingsRepository
                .existsByTrainingIdAndDateAfterAndLimitGreaterThan(
                        trainingId,
                        todayDateFormatted,
                        participantsCount
                );
        boolean isAbilityInTheTodayEvents = groupTrainingsRepository
                .existsByTrainingIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(
                        trainingId,
                        todayDateFormatted,
                        timeNowFormatted,
                        participantsCount
                );

        return isAbilityInTheFutureEvents || isAbilityInTheTodayEvents;
    }

    @Override
    public boolean isAbilityToCreateTraining(GroupTrainingRequest groupTrainingModel) {
        String date = groupTrainingModel.getDate();
        String startTime = groupTrainingModel.getStartTime();
        String endTime = groupTrainingModel.getEndTime();
        int hallNo = groupTrainingModel.getHallNo();

        Document eqDate = new Document("date", date);

        Document gtBeginning = new Document("$gt", startTime);
        Document gteBeginning = new Document("$gte", startTime);
        Document lteBeginning = new Document("$lte", startTime);
        Document startGteBeginning = new Document("startTime", gteBeginning);
        Document endGtBeginning = new Document("endTime", gtBeginning);
        Document startLteBeginning = new Document("startTime", lteBeginning);
        Document ltEnd = new Document("$lt", endTime);
        Document lteEnd = new Document("$lte", endTime);
        Document gteEnd = new Document("$gte", endTime);
        Document endLtEnd = new Document("endTime", lteEnd);
        Document endGteEnd = new Document("endTime", gteEnd);

        Document startLtEnd = new Document("startTime", ltEnd);
        Document eqHallNo = new Document("hallNo", hallNo);

        Document startDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, startGteBeginning, startLtEnd, eqHallNo));
        Document endDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, endGtBeginning, endLtEnd, eqHallNo));
        Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                eqDate, startLteBeginning, endGteEnd, eqHallNo));

        Document match = new Document("$match", new Document(
                "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
        List<Document> pipeline = Arrays.asList(match);

        MongoCollection collection = getGroupTrainingsCollection();
        return !collection.aggregate(pipeline).cursor().hasNext();
    }

    private MongoCollection getGroupTrainingsCollection() {
        MongoClient mongoClient = MongoClients.create(environment.getProperty("spring.data.mongodb.uri"));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("spring.data.mongodb.database"));
        return mongoDatabase.getCollection(GROUP_TRAININGS_COLLECTION_NAME);
    }

    @Override
    public boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequest groupTrainingModel) {
        String date = groupTrainingModel.getDate();
        String startTime = groupTrainingModel.getStartTime();
        String endTime = groupTrainingModel.getEndTime();
        int hallNo = groupTrainingModel.getHallNo();

        Document eqDate = new Document("date", date);

        Document gtBeginning = new Document("$gt", startTime);
        Document gteBeginning = new Document("$gte", startTime);
        Document lteBeginning = new Document("$lte", startTime);
        Document startGteBeginning = new Document("startTime", gteBeginning);
        Document endGtBeginning = new Document("endTime", gtBeginning);
        Document startLteBeginning = new Document("startTime", lteBeginning);
        Document ltEnd = new Document("$lt", endTime);
        Document lteEnd = new Document("$lte", endTime);
        Document gteEnd = new Document("$gte", endTime);
        Document startLtEnd = new Document("startTime", ltEnd);
        Document endLtEnd = new Document("endTime", lteEnd);
        Document endGteEnd = new Document("endTime", gteEnd);

        Document eqHallNo = new Document("hallNo", hallNo);
        Document notEqTrainingId = new Document("ne", new Document("trainingId", trainingId));

        Document startDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, startGteBeginning, startLtEnd, eqHallNo, notEqTrainingId));
        Document endDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, endGtBeginning, endLtEnd, eqHallNo, notEqTrainingId));
        Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                eqDate, startLteBeginning, endGteEnd, eqHallNo, notEqTrainingId));

        Document match = new Document("$match", new Document(
                "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
        List<Document> pipeline = Arrays.asList(match);

        MongoCollection collection = getGroupTrainingsCollection();
        return !collection.aggregate(pipeline).cursor().hasNext();
    }
}
