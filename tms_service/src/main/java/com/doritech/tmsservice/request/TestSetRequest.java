package com.doritech.tmsservice.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestSetRequest {

	private String testName;

	private String testDescription;

	private String testSetCode;

	private String setNo;

	private Long trainingId;

	private LocalDateTime startDateTime;

	private LocalDateTime endDateTime;

	private Integer timeLimitMinutes;

	private BigDecimal passingPercentage;

	private Boolean shuffleQuestions;

	private Boolean shuffleOptions;

	private Boolean isActive;

	public TestSetRequest() {
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

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
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
}