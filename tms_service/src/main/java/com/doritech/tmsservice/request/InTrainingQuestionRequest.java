package com.doritech.tmsservice.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InTrainingQuestionRequest {

	@NotNull(message = "{inTrainingQuestion.videoId.notnull}")
	private Long videoId;

	@NotNull(message = "{inTrainingQuestion.timestampSeconds.notnull}")
	@Min(value = 0, message = "{inTrainingQuestion.timestampSeconds.min}")
	private Integer timestampSeconds;

	@NotNull(message = "{inTrainingQuestion.questionText.notnull}")
	@NotBlank(message = "{inTrainingQuestion.questionText.notblank}")
	private String questionText;

	@Pattern(regexp = "^(MCQ|FILL_IN_BLANKS|ONE_WORD|DROPDOWN)$", message = "{inTrainingQuestion.questionType.pattern}")
	private String questionType;

	private List<String> options;

	@Size(max = 255, message = "{inTrainingQuestion.correctAnswer.size}")
	private String correctAnswer;

	@Min(value = 5, message = "{inTrainingQuestion.timerSeconds.min}")
	private Integer timerSeconds;

	private Boolean isRequired;

	@Min(value = 0, message = "{inTrainingQuestion.displayOrder.min}")
	private Integer displayOrder;

	public InTrainingQuestionRequest() {
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

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
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

	@Override
	public String toString() {
		return "InTrainingQuestionRequest [videoId=" + videoId + ", timestampSeconds=" + timestampSeconds
				+ ", questionType=" + questionType + "]";
	}
}