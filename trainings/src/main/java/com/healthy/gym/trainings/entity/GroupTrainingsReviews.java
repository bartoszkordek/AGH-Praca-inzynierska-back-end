package com.healthy.gym.trainings.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "GroupTrainingsReviews")
public class GroupTrainingsReviews {

    @Id
    @JsonProperty("_id")
    private String id;

    @JsonProperty("trainingName")
    private String trainingName;
    @JsonProperty("clientId")
    private String clientId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("stars")
    private int stars;
    @JsonProperty("text")
    private String text;

    public GroupTrainingsReviews(){

    }

    public GroupTrainingsReviews(String trainingName, String clientId, String date, int stars, String text){
        this.trainingName = trainingName;
        this.clientId = clientId;
        this.date = date;
        this.stars = stars;
        this.text = text;
    }

    @Override
    public String toString() {
        return "GroupTrainingsReviews{" +
                "id='" + id + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", date='" + date + '\'' +
                ", stars=" + stars +
                ", text='" + text + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDate() {
        return date;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTrainingId(String trainingId) {
        this.trainingName = trainingId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setText(String text) {
        this.text = text;
    }
}