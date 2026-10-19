
package com.doritech.tmsservice.request;

import java.time.LocalDateTime;

public class DirectTestAssignmentRequest {

	private Long testSetId;
	private Long userId;
	private Long batchId;
	private LocalDateTime dueDate;

	public DirectTestAssignmentRequest() {
	}

	public Long getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Long testSetId) {
		this.testSetId = testSetId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

}
