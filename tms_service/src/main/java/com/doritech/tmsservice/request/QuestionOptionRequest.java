package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QuestionOptionRequest {

    @NotNull(message = "{questionOption.testQuestionId.notnull}")
    private Long testQuestionId;

    @NotNull(message = "{questionOption.optionText.notnull}")
    @NotBlank(message = "{questionOption.optionText.notblank}")
    private String optionText;

    @Size(max = 10, message = "{questionOption.optionLabel.size}")
    private String optionLabel;

    private Boolean isCorrect;

    @Min(value = 0, message = "{questionOption.displayOrder.min}")
    private Integer displayOrder;

    public QuestionOptionRequest() {
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

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public String toString() {
        return "QuestionOptionRequest [testQuestionId=" + testQuestionId + ", optionLabel=" + optionLabel + "]";
    }
}