package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingAssignmentRequest;

public interface TrainingAssignmentService {

    ResponseEntity createTrainingAssignment(TrainingAssignmentRequest request);

    ResponseEntity getTrainingAssignmentById(Long id);

    ResponseEntity getAllTrainingAssignment(int page, int size, String sortBy, String sortDir);

    ResponseEntity getAssignmentsByUserId(Long userId);

    ResponseEntity getAssignmentsByTrainingId(Long trainingId);

    ResponseEntity startTrainingAssignment(Long id);

    ResponseEntity deleteTrainingAssignment(Long id);
}