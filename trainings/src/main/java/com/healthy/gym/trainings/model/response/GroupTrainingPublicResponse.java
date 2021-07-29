package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.validation.DateValidator;
import com.healthy.gym.trainings.validation.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class GroupTrainingPublicResponse {

    @NotNull
    private String trainingId;
    @NotNull
    private String trainingName;
    @NotNull
    private String trainerId;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
    @NotNull
    private boolean allDay;
    @NotNull
    private int hallNo;
    @NotNull
    private int limit;

    private double rating;

    public GroupTrainingPublicResponse(
            @JsonProperty("trainingId") String  trainingId,
            @JsonProperty("trainingName") String trainingName,
            @JsonProperty("trainerId") String trainerId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonProperty("date") String date,
            @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime,
            @JsonProperty("hallNo") int hallNo,
            @JsonProperty("limit") int limit,
            @JsonProperty("rating") double rating
    ) throws InvalidHourException, InvalidDateException {

        DateValidator dateValidator = new DateValidator();
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        this.trainingId = trainingId;
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        if (dateValidator.validate(date) && time24HoursValidator.validate(startTime)) {
            this.startDate = date.concat("T").concat(startTime);
        } else {
            throw new InvalidDateException("Wrong start date or time");
        }
        if(dateValidator.validate(date) && time24HoursValidator.validate(endTime)){
            this.endDate = date.concat("T").concat(endTime);
        } else {
            throw new InvalidHourException("Wrong end date or time");
        }
        this.allDay = false;
        this.hallNo = hallNo;
        this.limit = limit;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "GroupTrainingPublicResponse{" +
                "trainingId='" + trainingId + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", hallNo=" + hallNo +
                ", limit=" + limit +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingPublicResponse that = (GroupTrainingPublicResponse) o;
        return allDay == that.allDay &&
                hallNo == that.hallNo &&
                limit == that.limit &&
                Double.compare(that.rating, rating) == 0 &&
                Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(trainerId, that.trainerId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingId, trainingName, trainerId, startDate, endDate, allDay, hallNo, limit, rating);
    }

    public String getTrainingId() { return trainingId; }

    public String getTrainingName() {
        return trainingName;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getStartTime() {
        return startDate;
    }

    public String getEndTime() {
        return endDate;
    }

    public int getHallNo() {
        return hallNo;
    }

    public int getLimit() {
        return limit;
    }

    public double getRating() {
        return rating;
    }
}
