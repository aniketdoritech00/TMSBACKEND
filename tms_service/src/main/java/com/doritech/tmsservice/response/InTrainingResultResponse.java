package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.util.List;

public class InTrainingResultResponse {

	private Long trainingAssignmentId;

	private Long trainingId;

	private String trainingName;

	private Long videoId;

	private Integer totalQuestions;

	private Integer attemptedQuestions;

	private Integer correctAnswers;

	private Integer wrongAnswers;

	private Integer skippedQuestions;

	private BigDecimal overallScore;

	private BigDecimal passingPercentage;

	private Boolean passed;

	private String result;

	private List<InTrainingQuestionResultResponse> questions;

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
	}

	public String getTrainingName() {
		return trainingName;
	}

	public void setTrainingName(String trainingName) {
		this.trainingName = trainingName;
	}

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getAttemptedQuestions() {
		return attemptedQuestions;
	}

	public void setAttemptedQuestions(Integer attemptedQuestions) {
		this.attemptedQuestions = attemptedQuestions;
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

	public BigDecimal getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(BigDecimal overallScore) {
		this.overallScore = overallScore;
	}

	public BigDecimal getPassingPercentage() {
		return passingPercentage;
	}

	public void setPassingPercentage(BigDecimal passingPercentage) {
		this.passingPercentage = passingPercentage;
	}

	public Boolean getPassed() {
		return passed;
	}

	public void setPassed(Boolean passed) {
		this.passed = passed;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<InTrainingQuestionResultResponse> getQuestions() {
		return questions;
	}

	public void setQuestions(List<InTrainingQuestionResultResponse> questions) {
		this.questions = questions;
	}
}