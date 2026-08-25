package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TestAttemptRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TestAttemptService {

	ResponseEntity createTestAttempt(TestAttemptRequest request);

	ResponseEntity getTestAttemptById(Long testAttemptId);

	ResponseEntity getTestAttemptsByUserId(Long userId);

	ResponseEntity getTestAttemptsByTestSetAndUser(Long testSetId, Long userId);

	ResponseEntity getTestAttemptsByTrainingAssignment(Long trainingAssignmentId);

	ResponseEntity getUserTestAttemptsByTrainingAssignment(Long trainingAssignmentId, Long userId);

	ResponseEntity getInProgressTestAttempt(Long userId);

	ResponseEntity updateTestAttempt(Long testAttemptId, TestAttemptRequest request);

	ResponseEntity completeTestAttempt(Long testAttemptId);

	ResponseEntity abandonTestAttempt(Long testAttemptId);

	ResponseEntity incrementViolationCount(Long testAttemptId);

	ResponseEntity deleteTestAttempt(Long testAttemptId);
}