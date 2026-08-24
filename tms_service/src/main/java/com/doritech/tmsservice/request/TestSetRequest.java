package com.doritech.tmsservice.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TestSetRequest {

    @NotNull(message = "{testSet.name.notnull}")
    @NotBlank(message = "{testSet.name.notblank}")
    @Size(min = 2, max = 255, message = "{testSet.name.size}")
    private String testName;

    @Size(max = 2000, message = "{testSet.description.size}")
    private String testDescription;

    @Size(max = 50, message = "{testSet.code.size}")
    @Pattern(regexp = "^[A-Z0-9_-]*$", message = "{testSet.code.pattern}")
    private String testCode;

    @Size(max = 50, message = "{testSet.setNo.size}")
    private String setNo;

    private Long trainingId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Min(value = 1, message = "{testSet.timeLimitMinutes.min}")
    private Integer timeLimitMinutes;

    @DecimalMin(value = "0.0", message = "{testSet.passingPercentage.min}")
    @DecimalMax(value = "100.0", message = "{testSet.passingPercentage.max}")
    private BigDecimal passingPercentage;

    private Boolean shuffleQuestions;

    private Boolean shuffleOptions;

    private Boolean isActive;

    @NotNull(message = "{testSet.createdBy.notnull}")
    private Long createdBy;

    public TestSetRequest() {
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

    @Override
    public String toString() {
        return "TestSetRequest [testName=" + testName + ", testCode=" + testCode + ", trainingId=" + trainingId + "]";
    }
}