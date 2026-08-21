package com.doritech.tmsservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "in_training_questions")
public class InTrainingQuestion {

	public enum QuestionType {
		MCQ, FILL_IN_BLANKS, ONE_WORD, DROPDOWN
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private Long questionId;

	@Column(name = "video_id", nullable = false)
	private Long videoId;

	@Column(name = "timestamp_seconds", nullable = false)
	private Integer timestampSeconds;

	@Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type")
	private QuestionType questionType = QuestionType.MCQ;

	@Column(name = "options", columnDefinition = "JSON")
	private String options;

	@Column(name = "correct_answer", length = 255)
	private String correctAnswer;

	@Column(name = "timer_seconds")
	private Integer timerSeconds = 30;

	@Column(name = "is_required")
	private Boolean isRequired = true;

	@Column(name = "display_order")
	private Integer displayOrder = 0;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public InTrainingQuestion() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.questionType == null) {
			this.questionType = QuestionType.MCQ;
		}
		if (this.timerSeconds == null) {
			this.timerSeconds = 30;
		}
		if (this.isRequired == null) {
			this.isRequired = true;
		}
		if (this.displayOrder == null) {
			this.displayOrder = 0;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Integer getTimestampSeconds() {
		return timestampSeconds;
	}

	public void setTimestampSeconds(Integer timestampSeconds) {
		this.timestampSeconds = timestampSeconds;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Integer getTimerSeconds() {
		return timerSeconds;
	}

	public void setTimerSeconds(Integer timerSeconds) {
		this.timerSeconds = timerSeconds;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
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

	@Override
	public String toString() {
		return "InTrainingQuestion [questionId=" + questionId + ", videoId=" + videoId + ", questionType="
				+ questionType + "]";
	}
}