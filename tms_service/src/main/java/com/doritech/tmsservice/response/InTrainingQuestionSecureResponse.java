package com.doritech.tmsservice.response;

import java.util.List;

public class InTrainingQuestionSecureResponse {

	private Long questionId;
	private Long videoId;
	private Integer timestampSeconds;
	private String questionText;
	private String questionType;
	private List<String> options;
	private Integer timerSeconds;
	private Boolean isRequired;
	private Integer displayOrder;

	public InTrainingQuestionSecureResponse() {
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
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

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public Integer getTimerSeconds() {
		return timerSeconds;
	}

	public void setTimerSeconds(Integer timerSeconds) {
		this.timerSeconds = timerSeconds;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}