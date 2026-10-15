package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "test_sets")
public class TestSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_set_id")
	private Long testSetId;

	@Column(name = "test_name", nullable = false, length = 255)
	private String testName;

	@Column(name = "test_description", columnDefinition = "TEXT")
	private String testDescription;

	@Column(name = "test_set_code", unique = true, length = 50)
	private String testSetCode;

	@Column(name = "set_no", unique = true, length = 50)
	private String setNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_id")
	private Training training;

	@Column(name = "start_date_time")
	private LocalDateTime startDateTime;

	@Column(name = "end_date_time")
	private LocalDateTime endDateTime;

	@Column(name = "time_limit_minutes")
	private Integer timeLimitMinutes = 60;

	@Column(name = "passing_percentage", precision = 5, scale = 2)
	private BigDecimal passingPercentage = new BigDecimal("70.00");

	@Column(name = "shuffle_questions")
	private Boolean shuffleQuestions = true;

	@Column(name = "shuffle_options")
	private Boolean shuffleOptions = true;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	public Long getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Long testSetId) {
		this.testSetId = testSetId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestDescription() {
		return testDescription;
	}

	public void setTestDescription(String testDescription) {
		this.testDescription = testDescription;
	}

	public String getTestSetCode() {
		return testSetCode;
	}

	public void setTestSetCode(String testSetCode) {
		this.testSetCode = testSetCode;
	}

	public String getSetNo() {
		return setNo;
	}

	public void setSetNo(String setNo) {
		this.setNo = setNo;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Integer getTimeLimitMinutes() {
		return timeLimitMinutes;
	}

	public void setTimeLimitMinutes(Integer timeLimitMinutes) {
		this.timeLimitMinutes = timeLimitMinutes;
	}

	public BigDecimal getPassingPercentage() {
		return passingPercentage;
	}

	public void setPassingPercentage(BigDecimal passingPercentage) {
		this.passingPercentage = passingPercentage;
	}

	public Boolean getShuffleQuestions() {
		return shuffleQuestions;
	}

	public void setShuffleQuestions(Boolean shuffleQuestions) {
		this.shuffleQuestions = shuffleQuestions;
	}

	public Boolean getShuffleOptions() {
		return shuffleOptions;
	}

	public void setShuffleOptions(Boolean shuffleOptions) {
		this.shuffleOptions = shuffleOptions;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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

}