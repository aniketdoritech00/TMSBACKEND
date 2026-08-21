package com.doritech.tmsservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "training_categories")
public class TrainingCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_category_id")
	private Long trainingCategoryId;

	@Column(name = "category_name", nullable = false, length = 100)
	private String categoryName;

	@Column(name = "category_description", columnDefinition = "TEXT")
	private String categoryDescription;

	@Column(name = "category_code", unique = true, length = 50)
	private String categoryCode;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	public TrainingCategory() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getTrainingCategoryId() {
		return trainingCategoryId;
	}

	public void setTrainingCategoryId(Long trainingCategoryId) {
		this.trainingCategoryId = trainingCategoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "TrainingCategory [trainingCategoryId=" + trainingCategoryId + ", categoryName=" + categoryName + "]";
	}
}