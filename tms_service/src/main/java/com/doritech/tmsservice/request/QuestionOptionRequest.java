package com.doritech.tmsservice.request;

public class QuestionOptionRequest {

	private Long testQuestionId;

	private String optionText;

	private String optionLabel;

	private Boolean isCorrect;

	private Integer displayOrder;

	public QuestionOptionRequest() {
	}

	public Long getTestQuestionId() {
		return testQuestionId;
	}

	public void setTestQuestionId(Long testQuestionId) {
		this.testQuestionId = testQuestionId;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public String getOptionLabel() {
		return optionLabel;
	}

	public void setOptionLabel(String optionLabel) {
		this.optionLabel = optionLabel;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}