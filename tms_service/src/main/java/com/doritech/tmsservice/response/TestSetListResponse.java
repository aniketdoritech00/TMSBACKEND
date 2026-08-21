package com.doritech.tmsservice.response;

public class TestSetListResponse {

    private Long testSetId;
    private String testName;
    private String testCode;
    private Long trainingId;
    private Boolean isActive;

    public TestSetListResponse() {
    }

    public TestSetListResponse(Long testSetId, String testName, String testCode, Long trainingId, Boolean isActive) {
        this.testSetId = testSetId;
        this.testName = testName;
        this.testCode = testCode;
        this.trainingId = trainingId;
        this.isActive = isActive;
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

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}