package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestSetResponse {

    private Long testSetId;
    private String testName;
    private String testDescription;
    private String testCode;
    private String setNo;
    private Long trainingId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer timeLimitMinutes;
    private BigDecimal passingPercentage;
    private Boolean shuffleQuestions;
    private Boolean shuffleOptions;
    private Boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    public TestSetResponse() {
    }

    public Long getTestSetId() {
        return testSetId;
    }

    public void setTestSetId(Long testSetId) {
        this.testSetId = testSetId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getSetNo() {
        return setNo;
    }

    public void setSetNo(String setNo) {
        this.setNo = setNo;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public BigDecimal getPassingPercentage() {
        return passingPercentage;
    }

    public void setPassingPercentage(BigDecimal passingPercentage) {
        this.passingPercentage = passingPercentage;
    }

    public Boolean getShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public Boolean getShuffleOptions() {
        return shuffleOptions;
    }

    public void setShuffleOptions(Boolean shuffleOptions) {
        this.shuffleOptions = shuffleOptions;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "TestSetResponse [testSetId=" + testSetId + ", testName=" + testName + ", testCode=" + testCode + "]";
    }
}