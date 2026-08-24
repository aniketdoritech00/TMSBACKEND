package com.doritech.tmsservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_options")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_option_id")
    private Long questionOptionId;

    @Column(name = "test_question_id", nullable = false)
    private Long testQuestionId;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "option_label", length = 10)
    private String optionLabel;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    public QuestionOption() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.isCorrect == null) {
            this.isCorrect = false;
        }
        if (this.displayOrder == null) {
            this.displayOrder = 0;
        }
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
        return "QuestionOption [questionOptionId=" + questionOptionId + ", testQuestionId=" + testQuestionId + "]";
    }
}