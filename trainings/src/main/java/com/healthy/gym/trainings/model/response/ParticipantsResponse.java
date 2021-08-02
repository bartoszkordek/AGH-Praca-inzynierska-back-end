package com.healthy.gym.trainings.model.response;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ParticipantsResponse {

    @NotNull
    private String userId;

    @NotNull

    private String name;

    @NotNull

    private String surname;

    //TODO ADD Avatar

    public ParticipantsResponse() {
    }

    public ParticipantsResponse(String userId, String name, String surname) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantsResponse that = (ParticipantsResponse) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, surname);
    }

    @Override
    public String toString() {
        return "ParticipantsResponse{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
