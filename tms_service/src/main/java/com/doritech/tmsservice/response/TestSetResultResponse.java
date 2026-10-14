package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.util.List;

public class TestSetResultResponse {

	private Long testSetId;
	private String testName;

	private Long testAttemptId;
	private String result;
	private String status;

	private BigDecimal totalScore;
	private Integer totalQuestions;
	private Integer correctAnswers;
	private Integer wrongAnswers;
	private Integer skippedQuestions;
	private BigDecimal passingPercentage;

	private List<TestQuestionResultResponse> questions;

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

	public Long getTestAttemptId() {
		return testAttemptId;
	}

	public void setTestAttemptId(Long testAttemptId) {
		this.testAttemptId = testAttemptId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Integer getWrongAnswers() {
		return wrongAnswers;
	}

	public void setWrongAnswers(Integer wrongAnswers) {
		this.wrongAnswers = wrongAnswers;
	}

	public Integer getSkippedQuestions() {
		return skippedQuestions;
	}

	public void setSkippedQuestions(Integer skippedQuestions) {
		this.skippedQuestions = skippedQuestions;
	}

	public BigDecimal getPassingPercentage() {
		return passingPercentage;
	}

	public void setPassingPercentage(BigDecimal passingPercentage) {
		this.passingPercentage = passingPercentage;
	}

	public List<TestQuestionResultResponse> getQuestions() {
		return questions;
	}

	public void setQuestions(List<TestQuestionResultResponse> questions) {
		this.questions = questions;
	}

}