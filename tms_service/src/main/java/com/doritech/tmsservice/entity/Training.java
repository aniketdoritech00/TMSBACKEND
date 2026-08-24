package com.doritech.tmsservice.entity;

import java.math.BigDecimal;
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
@Table(name = "trainings")
public class Training {

	public enum TrainingType {
		REGULAR, MANDATORY, REFRESHER
	}

	public enum Status {
		DRAFT, PUBLISHED, ARCHIVED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_id")
	private Long trainingId;

	@Column(name = "training_code", nullable = false, unique = true, length = 50)
	private String trainingCode;

	@Column(name = "training_name", nullable = false, length = 255)
	private String trainingName;

	@Column(name = "training_description", columnDefinition = "TEXT")
	private String trainingDescription;

	@Enumerated(EnumType.STRING)
	@Column(name = "training_type")
	private TrainingType trainingType = TrainingType.REGULAR;

	@Column(name = "training_category_id")
	private Long trainingCategoryId;

	@Column(name = "trainer_id")
	private Long trainerId;

	@Column(name = "training_duration_days")
	private Integer trainingDurationDays = 1;

	@Column(name = "passing_percentage", precision = 5, scale = 2)
	private BigDecimal passingPercentage = new BigDecimal("70.00");

	@Column(name = "is_mandatory")
	private Boolean isMandatory = false;

	@Column(name = "parent_training_id")
	private Long parentTrainingId;

	@Column(name = "has_assessment")
	private Boolean hasAssessment = true;

	@Column(name = "has_video_assessment")
	private Boolean hasVideoAssessment = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.DRAFT;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	public Training() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.trainingType == null) {
			this.trainingType = TrainingType.REGULAR;
		}
		if (this.status == null) {
			this.status = Status.DRAFT;
		}
		if (this.trainingDurationDays == null) {
			this.trainingDurationDays = 1;
		}
		if (this.passingPercentage == null) {
			this.passingPercentage = new BigDecimal("70.00");
		}
		if (this.isMandatory == null) {
			this.isMandatory = false;
		}
		if (this.hasAssessment == null) {
			this.hasAssessment = true;
		}
		if (this.hasVideoAssessment == null) {
			this.hasVideoAssessment = false;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
	}

	public String getTrainingCode() {
		return trainingCode;
	}

	public void setTrainingCode(String trainingCode) {
		this.trainingCode = trainingCode;
	}

	public String getTrainingName() {
		return trainingName;
	}

	public void setTrainingName(String trainingName) {
		this.trainingName = trainingName;
	}

	public String getTrainingDescription() {
		return trainingDescription;
	}

	public void setTrainingDescription(String trainingDescription) {
		this.trainingDescription = trainingDescription;
	}

	public TrainingType getTrainingType() {
		return trainingType;
	}

	public void setTrainingType(TrainingType trainingType) {
		this.trainingType = trainingType;
	}

	public Long getTrainingCategoryId() {
		return trainingCategoryId;
	}

	public void setTrainingCategoryId(Long trainingCategoryId) {
		this.trainingCategoryId = trainingCategoryId;
	}

	public Long getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(Long trainerId) {
		this.trainerId = trainerId;
	}

	public Integer getTrainingDurationDays() {
		return trainingDurationDays;
	}

	public void setTrainingDurationDays(Integer trainingDurationDays) {
		this.trainingDurationDays = trainingDurationDays;
	}

	public BigDecimal getPassingPercentage() {
		return passingPercentage;
	}

	public void setPassingPercentage(BigDecimal passingPercentage) {
		this.passingPercentage = passingPercentage;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public Long getParentTrainingId() {
		return parentTrainingId;
	}

	public void setParentTrainingId(Long parentTrainingId) {
		this.parentTrainingId = parentTrainingId;
	}

	public Boolean getHasAssessment() {
		return hasAssessment;
	}

	public void setHasAssessment(Boolean hasAssessment) {
		this.hasAssessment = hasAssessment;
	}

	public Boolean getHasVideoAssessment() {
		return hasVideoAssessment;
	}

	public void setHasVideoAssessment(Boolean hasVideoAssessment) {
		this.hasVideoAssessment = hasVideoAssessment;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
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

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	@Override
	public String toString() {
		return "Training [trainingId=" + trainingId + ", trainingCode=" + trainingCode + ", trainingName="
				+ trainingName + ", status=" + status + "]";
	}
}