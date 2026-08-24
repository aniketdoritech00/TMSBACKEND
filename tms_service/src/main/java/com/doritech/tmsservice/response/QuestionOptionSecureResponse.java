package com.doritech.tmsservice.response;

public class QuestionOptionSecureResponse {

    private Long questionOptionId;
    private Long testQuestionId;
    private String optionText;
    private String optionLabel;
    private Integer displayOrder;

    public QuestionOptionSecureResponse() {
    }

    public QuestionOptionSecureResponse(Long questionOptionId, Long testQuestionId, String optionText,
            String optionLabel, Integer displayOrder) {
        this.questionOptionId = questionOptionId;
        this.testQuestionId = testQuestionId;
        this.optionText = optionText;
        this.optionLabel = optionLabel;
        this.displayOrder = displayOrder;
    }

    public Long getQuestionOptionId() {
        return questionOptionId;
    }

    public void setQuestionOptionId(Long questionOptionId) {
        this.questionOptionId = questionOptionId;
    }

    public Long getTestQuestionId() {
        return testQuestionId;
    }

    public void setTestQuestionId(Long testQuestionId) {
        this.testQuestionId = testQuestionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}