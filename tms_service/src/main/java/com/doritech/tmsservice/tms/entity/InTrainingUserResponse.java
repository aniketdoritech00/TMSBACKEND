package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "in_training_user_responses")
public class InTrainingUserResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "in_training_user_response_id")
	private Long inTrainingUserResponseId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	private InTrainingQuestion question;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_assignment_id")
	private TrainingAssignment trainingAssignment;

	@Column(name = "user_answer", length = 255)
	private String userAnswer;

	@Column(name = "is_correct", columnDefinition = "boolean default false")
	private Boolean isCorrect = false;

	@Column(name = "is_skipped", columnDefinition = "boolean default false")
	private Boolean isSkipped = false;

	@Column(name = "time_taken_seconds")
	private Integer timeTakenSeconds;

	@CreationTimestamp
	@Column(name = "responded_at", updatable = false)
	private LocalDateTime respondedAt;

	public Long getInTrainingUserResponseId() {
		return inTrainingUserResponseId;
	}

	public void setInTrainingUserResponseId(Long inTrainingUserResponseId) {
		this.inTrainingUserResponseId = inTrainingUserResponseId;
	}

	public InTrainingQuestion getQuestion() {
		return question;
	}

	public void setQuestion(InTrainingQuestion question) {
		this.question = question;
	}

	public TrainingAssignment getTrainingAssignment() {
		return trainingAssignment;
	}

	public void setTrainingAssignment(TrainingAssignment trainingAssignment) {
		this.trainingAssignment = trainingAssignment;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Boolean getIsSkipped() {
		return isSkipped;
	}

	public void setIsSkipped(Boolean isSkipped) {
		this.isSkipped = isSkipped;
	}

	public Integer getTimeTakenSeconds() {
		return timeTakenSeconds;
	}

	public void setTimeTakenSeconds(Integer timeTakenSeconds) {
		this.timeTakenSeconds = timeTakenSeconds;
	}

	public LocalDateTime getRespondedAt() {
		return respondedAt;
	}

	public void setRespondedAt(LocalDateTime respondedAt) {
		this.respondedAt = respondedAt;
	}

}