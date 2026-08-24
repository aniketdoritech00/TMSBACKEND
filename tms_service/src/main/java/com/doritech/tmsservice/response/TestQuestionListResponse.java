package com.doritech.tmsservice.response;

import java.math.BigDecimal;

public class TestQuestionListResponse {

    private Long testQuestionId;
    private Long testSetId;
    private String questionType;
    private BigDecimal marks;
    private Integer displayOrder;

    public TestQuestionListResponse() {
    }

    public TestQuestionListResponse(Long testQuestionId, Long testSetId, String questionType,
            BigDecimal marks, Integer displayOrder) {
        this.testQuestionId = testQuestionId;
        this.testSetId = testSetId;
        this.questionType = questionType;
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
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