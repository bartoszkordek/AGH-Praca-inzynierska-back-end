package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.config.MongoConfig;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class GroupTrainingsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private GroupTrainingsRepository groupTrainingsRepository;

    @Autowired
    private MongoConfig mongoConfig;

    private MongoClient mongoClient;
    private MongoDatabase mdb;

    public List<GroupTrainings> getGroupTrainings(){
        return groupTrainingsRepository.findAll();
    }

    public GroupTrainings getGroupTrainingById(String trainingId){
        return groupTrainingsRepository.findFirstById(trainingId);
    }

    public List<String> getTrainingParticipants(String trainingId){
        return groupTrainingsRepository.getFirstById(trainingId).getParticipants();
    }

    public boolean isGroupTrainingExist(String trainingId){
        return groupTrainingsRepository.existsById(trainingId);
    }

    public boolean isAbilityToGroupTrainingEnrollment(String trainingId){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        String timeNowFormatted = sdfTime.format(now);

        if(!groupTrainingsRepository.existsById(trainingId)) return false;

        int participantsCount = groupTrainingsRepository.getFirstById(trainingId).getParticipants().size();

        boolean isAbilityInTheFutureEvents = groupTrainingsRepository.existsByIdAndDateAfterAndLimitGreaterThan(trainingId,
                todayDateFormatted, participantsCount);
        boolean isAbilityInTheTodayEvents = groupTrainingsRepository.existsByIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(
                trainingId, todayDateFormatted, timeNowFormatted, participantsCount);

        return isAbilityInTheFutureEvents || isAbilityInTheTodayEvents;
    }

    public boolean isClientAlreadyEnrolledToGroupTraining(String trainingId, String clientId){
        return groupTrainingsRepository.getFirstById(trainingId).getParticipants().contains(clientId);
    }

    public void enrollToGroupTraining(String trainingId, String participantId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        List<String> participants = groupTrainings.getParticipants();
        participants.add(participantId);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public boolean isAbilityToCreateTraining(GroupTrainingModel groupTrainingModel) throws ParseException {
        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase(environment.getProperty("microservice.db.name"));
        MongoCollection collection = mdb.getCollection(environment.getProperty("microservice.db.collection"));

        String trainingName = groupTrainingModel.getTrainingName();
        String trainerId = groupTrainingModel.getTrainerId();
        String date = groupTrainingModel.getDate();
        String startTime = groupTrainingModel.getStartTime();
        String endTime = groupTrainingModel.getEndTime();
        int hallNo = groupTrainingModel.getHallNo();
        int limit = groupTrainingModel.getLimit();

        if(trainingName.isEmpty() || trainerId.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()
                || hallNo <= 0 || limit <=0) return false;

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDateParsed = sdfDate.parse(date);
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        Date todayDateParsed = sdfDate.parse(todayDateFormatted);
        if(requestDateParsed.before(todayDateParsed)) return false;

        LocalTime start = LocalTime.parse( startTime);
        LocalTime stop = LocalTime.parse( endTime );
        Duration duration = Duration.between( start, stop );

        if(duration.toMinutes()<=0) return false;

        Document eqDate = new Document("date", date);

        Document gtBeginning = new Document("$gt", startTime);
        Document gteBeginning = new Document("$gte", startTime);
        Document lteBeginning = new Document("$lte", startTime);
        Document startGteBeginning = new Document("startTime", gteBeginning);
        Document endGtBeginning = new Document("endTime", gtBeginning);
        Document startLteBeginning = new Document("startTime",lteBeginning);
        Document ltEnd = new Document("$lt", endTime);
        Document lteEnd = new Document("$lte", endTime);
        Document gteEnd = new Document("$gte", endTime);
        Document startLtEnd = new Document("startTime", ltEnd);
        Document endLtEnd = new Document("endTime", lteEnd);
        Document endGteEnd = new Document("endTime", gteEnd);

        Document eqHallNo = new Document("hallNo", hallNo);

        Document middleTimeEventValid = new Document("$and", Arrays.asList(
                eqDate, startGteBeginning, startLtEnd, endGtBeginning, endLtEnd, eqHallNo));
        Document startDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, startGteBeginning, startLtEnd, eqHallNo));
        Document endDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, endGtBeginning, endLtEnd, eqHallNo));
        Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                eqDate, startLteBeginning, endGteEnd, eqHallNo));

        Document match = new Document("$match", new Document(
                "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
        List<Document> pipeline = Arrays.asList(match);

        return !collection.aggregate(pipeline).cursor().hasNext();
    }

    public GroupTrainings createTraining(GroupTrainingModel groupTrainingModel){
        GroupTrainings response = groupTrainingsRepository.insert(new GroupTrainings(
                groupTrainingModel.getTrainingName(),
                groupTrainingModel.getTrainerId(),
                groupTrainingModel.getDate(),
                groupTrainingModel.getStartTime(),
                groupTrainingModel.getEndTime(),
                groupTrainingModel.getHallNo(),
                groupTrainingModel.getLimit(),
                groupTrainingModel.getParticipants(),
                groupTrainingModel.getReserveList()
        ));
        return response;
    }

    public GroupTrainings removeTraining(String trainingId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        groupTrainingsRepository.removeById(trainingId);
        return groupTrainings;
    }

    public GroupTrainings updateTraining(String trainingId, GroupTrainingModel groupTrainingModelRequest){
        boolean ifExistGroupTraining = groupTrainingsRepository.existsById(trainingId);

        GroupTrainings groupTrainings = null;
        if(ifExistGroupTraining){
            groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
            groupTrainings.setTrainingName(groupTrainingModelRequest.getTrainingName());
            groupTrainings.setTrainerId(groupTrainingModelRequest.getTrainerId());
            groupTrainings.setDate(groupTrainingModelRequest.getDate());
            groupTrainings.setStartTime(groupTrainingModelRequest.getStartTime());
            groupTrainings.setEndTime(groupTrainingModelRequest.getEndTime());
            groupTrainings.setHallNo(groupTrainingModelRequest.getHallNo());
            groupTrainings.setLimit(groupTrainingModelRequest.getLimit());
            GroupTrainings response = groupTrainingsRepository.save(groupTrainings);
        }

        return groupTrainings;
    }
}
