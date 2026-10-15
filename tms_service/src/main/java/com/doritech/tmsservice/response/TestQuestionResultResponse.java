package com.doritech.tmsservice.response;

import java.util.List;

public class TestQuestionResultResponse {

	private Long testQuestionId;
	private String question;
	private String questionType;

	private List<TestQuestionOptionResponse> options;

	private String correctAnswer;
	private String userAnswer;
	private Boolean isCorrect;

	public Long getTestQuestionId() {
		return testQuestionId;
	}

	public void setTestQuestionId(Long testQuestionId) {
		this.testQuestionId = testQuestionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public List<TestQuestionOptionResponse> getOptions() {
		return options;
	}

	public void setOptions(List<TestQuestionOptionResponse> options) {
		this.options = options;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
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

}