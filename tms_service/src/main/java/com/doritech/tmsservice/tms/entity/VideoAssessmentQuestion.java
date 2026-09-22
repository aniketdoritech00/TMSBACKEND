package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.QuestionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_assessment_questions", indexes = {
		@Index(name = "idx_video_assessment_question_training", columnList = "training_id") })
public class VideoAssessmentQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_assessment_question_id")
	private Long videoAssessmentQuestionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_id", nullable = false)
	private Training training;

	@Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type")
	private QuestionType questionType = QuestionType.ORAL;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	private Long createBy;

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Long getVideoAssessmentQuestionId() {
		return videoAssessmentQuestionId;
	}

	public void setVideoAssessmentQuestionId(Long videoAssessmentQuestionId) {
		this.videoAssessmentQuestionId = videoAssessmentQuestionId;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
