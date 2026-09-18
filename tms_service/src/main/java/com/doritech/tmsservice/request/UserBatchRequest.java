package com.doritech.tmsservice.request;

import java.util.List;

public class UserBatchRequest {

	private List<Long> userIds;

	private Long batchId;

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
}