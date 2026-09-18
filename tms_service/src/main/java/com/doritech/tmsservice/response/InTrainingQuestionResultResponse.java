package com.doritech.tmsservice.response;

public class InTrainingQuestionResultResponse {

	private Long questionId;

	private Integer timestampSeconds;

	private String questionText;

	private String questionType;

	private String options;

	private String userAnswer;

	private String correctAnswer;

	private Boolean attempted;

	private Boolean correct;

	private Boolean skipped;

	private Integer timeTakenSeconds;

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Integer getTimestampSeconds() {
		return timestampSeconds;
	}

	public void setTimestampSeconds(Integer timestampSeconds) {
		this.timestampSeconds = timestampSeconds;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Boolean getAttempted() {
		return attempted;
	}

	public void setAttempted(Boolean attempted) {
		this.attempted = attempted;
	}

	public Boolean getCorrect() {
		return correct;
	}

	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}

	public Boolean getSkipped() {
		return skipped;
	}

	public void setSkipped(Boolean skipped) {
		this.skipped = skipped;
	}

	public Integer getTimeTakenSeconds() {
		return timeTakenSeconds;
	}

	public void setTimeTakenSeconds(Integer timeTakenSeconds) {
		this.timeTakenSeconds = timeTakenSeconds;
	}
}