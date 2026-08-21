package com.doritech.tmsservice.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TestQuestionRequest {

    @NotNull(message = "{testQuestion.testSetId.notnull}")
    private Long testSetId;

    private Integer questionGroupId;

    @NotNull(message = "{testQuestion.questionText.notnull}")
    @NotBlank(message = "{testQuestion.questionText.notblank}")
    private String questionText;

    @Pattern(regexp = "^(MCQ|DROPDOWN|FILL_IN_BLANKS|ONE_WORD)$", message = "{testQuestion.questionType.pattern}")
    private String questionType;

    @Size(max = 255, message = "{testQuestion.correctAnswer.size}")
    private String correctAnswer;

    @Min(value = 5, message = "{testQuestion.timeLimitSeconds.min}")
    private Integer timeLimitSeconds;

    @DecimalMin(value = "0.01", message = "{testQuestion.marks.min}")
    private BigDecimal marks;

    @Min(value = 0, message = "{testQuestion.displayOrder.min}")
    private Integer displayOrder;

    public TestQuestionRequest() {
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
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

    @Override
    public String toString() {
        return "TestQuestionRequest [testSetId=" + testSetId + ", questionType=" + questionType + "]";
    }
}