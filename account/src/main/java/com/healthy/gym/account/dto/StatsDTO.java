package com.healthy.gym.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

public class StatsDTO {
    @JsonProperty("day")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
    private int quantity;

    public StatsDTO() {
    }

    public StatsDTO(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public StatsDTO(LocalDate createdAt, int quantity) {
        this.createdAt = createdAt;
        this.quantity = quantity;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsDTO statsDTO = (StatsDTO) o;
        return quantity == statsDTO.quantity && Objects.equals(createdAt, statsDTO.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, quantity);
    }

    @Override
    public String toString() {
        return "StatsDTO{" +
                "createdAt=" + createdAt +
                ", quantity=" + quantity +
                '}';
    }
}
