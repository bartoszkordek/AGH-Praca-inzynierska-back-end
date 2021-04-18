package com.healthy.gym.trainings.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

public class IndividualTrainings {

    @Id
    @JsonProperty("_id")
    private String id;

    @JsonProperty("clientId")
    private String clientId;
    @JsonProperty("trainerId")
    private String trainerId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("endTime")
    private String endTime;
    @JsonProperty("hallNo")
    private int hallNo;
    @JsonProperty("remarks")
    private String remarks;

    public IndividualTrainings(){

    }

    public IndividualTrainings(String id, String clientId, String trainerId, String date,
                               String startTime, String endTime, int hallNo, String remarks){
        this.id = id;
        this.clientId = clientId;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hallNo = hallNo;
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "IndividualTrainings{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", hallNo=" + hallNo +
                ", remarks='" + remarks + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getHallNo() {
        return hallNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setHallNo(int hallNo) {
        this.hallNo = hallNo;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
