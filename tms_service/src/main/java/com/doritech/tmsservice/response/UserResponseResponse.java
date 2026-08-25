package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class UserResponseResponse {

	private Long userResponseId;

	private Long testAttemptId;

	private Long testQuestionId;

	private String userAnswer;

	private Boolean isCorrect;

	private Integer timeTakenSeconds;

	private LocalDateTime responseDate;

	public UserResponseResponse() {
	}

	public Long getUserResponseId() {
		return userResponseId;
	}

	public void setUserResponseId(Long userResponseId) {
		this.userResponseId = userResponseId;
	}

	public Long getTestAttemptId() {
		return testAttemptId;
	}

	public void setTestAttemptId(Long testAttemptId) {
		this.testAttemptId = testAttemptId;
	}

	public Long getTestQuestionId() {
		return testQuestionId;
	}

	public void setTestQuestionId(Long testQuestionId) {
		this.testQuestionId = testQuestionId;
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

	public Integer getTimeTakenSeconds() {
		return timeTakenSeconds;
	}

	public void setTimeTakenSeconds(Integer timeTakenSeconds) {
		this.timeTakenSeconds = timeTakenSeconds;
	}

	public LocalDateTime getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(LocalDateTime responseDate) {
		this.responseDate = responseDate;
	}
}