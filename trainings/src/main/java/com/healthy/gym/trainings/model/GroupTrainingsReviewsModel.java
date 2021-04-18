package com.healthy.gym.trainings.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupTrainingsReviewsModel {

    private String trainingName;
    private int stars;
    private String text;

    public GroupTrainingsReviewsModel(@JsonProperty("trainingName") String trainingName,
                                      @JsonProperty("stars") int stars,
                                      @JsonProperty("text") String text){
        this.trainingName = trainingName;
        this.stars = stars;
        this.text = text;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }
}