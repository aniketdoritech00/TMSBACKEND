package com.doritech.tmsservice.response;

import java.math.BigDecimal;

public class TestQuestionSecureResponse {

    private Long testQuestionId;
    private Long testSetId;
    private Integer questionGroupId;
    private String questionText;
    private String questionType;
    private Integer timeLimitSeconds;
    private BigDecimal marks;
    private Integer displayOrder;

    public TestQuestionSecureResponse() {
    }

    public TestQuestionSecureResponse(Long testQuestionId, Long testSetId, Integer questionGroupId,
            String questionText, String questionType, Integer timeLimitSeconds,
            BigDecimal marks, Integer displayOrder) {
        this.testQuestionId = testQuestionId;
        this.testSetId = testSetId;
        this.questionGroupId = questionGroupId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.timeLimitSeconds = timeLimitSeconds;
        this.marks = marks;
        this.displayOrder = displayOrder;
    }

    public Long getTestQuestionId() {
        return testQuestionId;
    }

    public void setTestQuestionId(Long testQuestionId) {
        this.testQuestionId = testQuestionId;
    }

    public Long getTestSetId() {
        return testSetId;
    }

    public void setTestSetId(Long testSetId) {
        this.testSetId = testSetId;
    }

    public Integer getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Integer questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(Integer timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public BigDecimal getMarks() {
        return marks;
    }

    public void setMarks(BigDecimal marks) {
        this.marks = marks;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}