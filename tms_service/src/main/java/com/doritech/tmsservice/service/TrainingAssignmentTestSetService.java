package com.doritech.tmsservice.service;

import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingAssignmentTestSetService {

	ResponseEntity getTestSetByTrainingAssignmentId(Long trainingAssignmentId);
}
