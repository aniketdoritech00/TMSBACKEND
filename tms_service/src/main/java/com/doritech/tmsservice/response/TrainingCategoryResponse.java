package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class TrainingCategoryResponse {

    private Long trainingCategoryId;
    private String categoryName;
    private String categoryDescription;
    private String categoryCode;
    private LocalDateTime createdAt;

    public TrainingCategoryResponse() {
    }

    public Long getTrainingCategoryId() {
        return trainingCategoryId;
    }

    public void setTrainingCategoryId(Long trainingCategoryId) {
        this.trainingCategoryId = trainingCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TrainingCategoryResponse [trainingCategoryId=" + trainingCategoryId
                + ", categoryName=" + categoryName + "]";
    }
}