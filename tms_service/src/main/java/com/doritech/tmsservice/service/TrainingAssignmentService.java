package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TrainingAssignmentRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingAssignmentService {

	ResponseEntity createTrainingAssignment(TrainingAssignmentRequest request);

	ResponseEntity getTrainingAssignmentById(Long id);

	ResponseEntity getAllTrainingAssignments();

	ResponseEntity getTrainingAssignmentsByTrainingId(Long trainingId);

	ResponseEntity getTrainingAssignmentsByUserId(Long userId);

	ResponseEntity updateTrainingAssignment(Long id, TrainingAssignmentRequest request);

	ResponseEntity deleteTrainingAssignment(Long id);

	ResponseEntity getAllTrainingAssignments(int page, int size, String sortBy, String sortDir);

	ResponseEntity getTrainingAssignmentsByTrainingId(Long trainingId, int page, int size, String sortBy,
			String sortDir);

	ResponseEntity getTrainingAssignmentsByUserId(Long userId, int page, int size, String sortBy, String sortDir);

	ResponseEntity getTrainingAssignmentsForCurrentUser();

	ResponseEntity getTrainingAssignmentsForCurrentUser(int page, int size, String sortBy, String sortDir);

	ResponseEntity updateTrainingAssignmentProgress(Long trainingAssignmentId);
}