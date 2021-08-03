package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.shared.GetGroupTrainingDTO;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class GroupTrainingResponse {

    @NotNull
    @JsonProperty("data")
    private final GetGroupTrainingDTO groupTraining;

    public GroupTrainingResponse(
            @JsonProperty("data") GetGroupTrainingDTO groupTraining

    ) {
        this.groupTraining = groupTraining;
    }

    public GetGroupTrainingDTO getGroupTraining() {
        return groupTraining;
    }

    @Override
    public String toString() {
        return "GroupTrainingResponse{" +
                "groupTraining=" + groupTraining +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingResponse that = (GroupTrainingResponse) o;
        return Objects.equals(groupTraining, that.groupTraining);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupTraining);
    }
}
