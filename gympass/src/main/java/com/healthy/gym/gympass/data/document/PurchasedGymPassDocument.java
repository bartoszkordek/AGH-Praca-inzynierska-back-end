package com.healthy.gym.gympass.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "purchasedGymPasses")
public class PurchasedGymPassDocument {

    @Id
    private String id;
    private String purchasedGymPassDocumentId;
    private GymPassDocument gymPassOffer;
    @DBRef
    private UserDocument user;
    private LocalDateTime purchaseDateTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private int entries;
    private LocalDate suspensionDate;

    public PurchasedGymPassDocument() {
        //empty constructor required spring data mapper
    }

    public PurchasedGymPassDocument(
            String purchasedGymPassDocumentId,
            GymPassDocument gymPassOffer,
            UserDocument user,
            LocalDateTime purchaseDateTime,
            LocalDate startDate,
            LocalDate endDate,
            int entries,
            LocalDate suspensionDate
    ) {
        this.purchasedGymPassDocumentId = purchasedGymPassDocumentId;
        this.gymPassOffer = gymPassOffer;
        this.user = user;
        this.purchaseDateTime = purchaseDateTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
        this.suspensionDate = suspensionDate;
    }

    public PurchasedGymPassDocument(
            String purchasedGymPassDocumentId,
            GymPassDocument gymPassOffer,
            UserDocument user,
            LocalDateTime purchaseDateTime,
            LocalDate startDate,
            LocalDate endDate,
            int entries
    ) {
        this.purchasedGymPassDocumentId = purchasedGymPassDocumentId;
        this.gymPassOffer = gymPassOffer;
        this.user = user;
        this.purchaseDateTime = purchaseDateTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPurchasedGymPassDocumentId() {
        return purchasedGymPassDocumentId;
    }

    public void setPurchasedGymPassDocumentId(String purchasedGymPassDocumentId) {
        this.purchasedGymPassDocumentId = purchasedGymPassDocumentId;
    }

    public GymPassDocument getGymPassOffer() {
        return gymPassOffer;
    }

    public void setGymPassOffer(GymPassDocument gymPassOffer) {
        this.gymPassOffer = gymPassOffer;
    }

    public UserDocument getUser() {
        return user;
    }

    public void setUser(UserDocument user) {
        this.user = user;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(LocalDateTime purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getEntries() {
        return entries;
    }

    public void setEntries(int entries) {
        this.entries = entries;
    }

    public LocalDate getSuspensionDate() {
        return suspensionDate;
    }

    public void setSuspensionDate(LocalDate suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    @Override
    public String toString() {
        return "PurchasedGymPassDocument{" +
                "id='" + id + '\'' +
                ", purchasedGymPassDocumentId='" + purchasedGymPassDocumentId + '\'' +
                ", gymPassOffer=" + gymPassOffer +
                ", user=" + user +
                ", purchaseDateTime=" + purchaseDateTime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", entries=" + entries +
                ", suspensionDate=" + suspensionDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchasedGymPassDocument that = (PurchasedGymPassDocument) o;
        return entries == that.entries &&
                Objects.equals(id, that.id) &&
                Objects.equals(purchasedGymPassDocumentId, that.purchasedGymPassDocumentId) &&
                Objects.equals(gymPassOffer, that.gymPassOffer) &&
                Objects.equals(user, that.user) &&
                Objects.equals(purchaseDateTime, that.purchaseDateTime) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(suspensionDate, that.suspensionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                purchasedGymPassDocumentId,
                gymPassOffer,
                user,
                purchaseDateTime,
                startDate,
                endDate,
                entries,
                suspensionDate
        );
    }
}
