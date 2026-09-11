	package com.doritech.tmsservice.request;
	
	public class ObserverAssignmentRequest {
	
		private Long trainingAssignmentId;
		private Long userId;
	
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
	}
