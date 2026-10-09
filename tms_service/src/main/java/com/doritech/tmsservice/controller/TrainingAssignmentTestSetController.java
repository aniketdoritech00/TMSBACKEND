package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.service.TrainingAssignmentTestSetService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/test-assignment-test-set")
public class TrainingAssignmentTestSetController {

	private final TrainingAssignmentTestSetService trainingAssignmentTestSetService;

	public TrainingAssignmentTestSetController(TrainingAssignmentTestSetService trainingAssignmentTestSetService) {
		this.trainingAssignmentTestSetService = trainingAssignmentTestSetService;
	}

	@GetMapping("/getTestSetByTrainingAssignmentId/{trainingAssignmentId}")
	public ResponseEntity getTestSetByTrainingAssignmentId(@PathVariable Long trainingAssignmentId) {
		return trainingAssignmentTestSetService.getTestSetByTrainingAssignmentId(trainingAssignmentId);
	}
}
