package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingTypeManagerResponse extends TrainingTypePublicResponse {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("avatar")
    private byte[] avatar;

    public TrainingTypeManagerResponse(@JsonProperty("_id") String id,
                                       @JsonProperty("trainingName") String trainingName,
                                       @JsonProperty("description") String description,
                                       @JsonProperty("avatar") byte[] avatar) {
        super(trainingName, description, avatar);
        this.id = id;
        this.trainingName = trainingName;
        this.description = description;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getTrainingName() {
        return trainingName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public byte[] getAvatar() {
        return avatar;
    }
}