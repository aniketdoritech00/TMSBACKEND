package com.doritech.tmsservice.service;

import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingResultService {
	ResponseEntity getResultByTrainingAssignmentId(Long trainingAssignmentId);
}
