package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TestAttemptRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TestAttemptService {

	ResponseEntity createTestAttempt(TestAttemptRequest request);

	ResponseEntity getTestAttemptById(Long testAttemptId);

	ResponseEntity getAllTestAttempts(int page, int size, String sortBy, String sortDir);

	ResponseEntity getTestAttemptsByUserId(Long userId, int page, int size, String sortBy, String sortDir);

	ResponseEntity getTestAttemptsByTestSetAndUser(Long testSetId, Long userId, int page, int size, String sortBy,
			String sortDir);

	ResponseEntity getTestAttemptsByTrainingAssignment(Long trainingAssignmentId, int page, int size, String sortBy,
			String sortDir);

	ResponseEntity getUserTestAttemptsByTrainingAssignment(Long trainingAssignmentId, Long userId, int page, int size,
			String sortBy, String sortDir);

	ResponseEntity getInProgressTestAttempt(Long userId);

	ResponseEntity updateTestAttempt(Long testAttemptId, TestAttemptRequest request);

	ResponseEntity completeTestAttempt(Long testAttemptId);

	ResponseEntity abandonTestAttempt(Long testAttemptId);

	ResponseEntity incrementViolationCount(Long testAttemptId);

	ResponseEntity deleteTestAttempt(Long testAttemptId);
}