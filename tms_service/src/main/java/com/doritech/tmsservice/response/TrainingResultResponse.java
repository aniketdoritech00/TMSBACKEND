package com.doritech.tmsservice.response;

public class TrainingResultResponse {

	private Long trainingAssignmentId;

	private Long userId;

	private Long trainingId;

	private String trainingName;

	private InTrainingResultResponse inTrainingResult;

	private TestSetResultResponse testSet;

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public InTrainingResultResponse getInTrainingResult() {
		return inTrainingResult;
	}

	public void setInTrainingResult(InTrainingResultResponse inTrainingResult) {
		this.inTrainingResult = inTrainingResult;
	}

	public TestSetResultResponse getTestSet() {
		return testSet;
	}

	public void setTestSet(TestSetResultResponse testSet) {
		this.testSet = testSet;
	}
}