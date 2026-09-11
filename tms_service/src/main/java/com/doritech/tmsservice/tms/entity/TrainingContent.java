package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.ContentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "training_content")
public class TrainingContent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_content_id")
	private Long trainingContentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_id", nullable = false)
	private Training training;

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private ContentType contentType;

	@Column(name = "content_reference_id")
	private Long contentReferenceId;

	@Column(name = "is_required")
	private boolean isRequired;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Long getTrainingContentId() {
		return trainingContentId;
	}

	public void setTrainingContentId(Long trainingContentId) {
		this.trainingContentId = trainingContentId;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public Long getContentReferenceId() {
		return contentReferenceId;
	}

	public void setContentReferenceId(Long contentReferenceId) {
		this.contentReferenceId = contentReferenceId;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
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

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

}