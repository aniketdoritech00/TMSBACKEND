package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.TrainingStatus;
import com.doritech.tmsservice.enums.TrainingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trainings")
public class Training {

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
	private TrainingStatus status = TrainingStatus.DRAFT;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

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

	public TrainingStatus getStatus() {
		return status;
	}

	public void setStatus(TrainingStatus status) {
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

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

}