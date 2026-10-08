package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.service.TrainingResultService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/training-result")
public class TrainingResultController {

	private TrainingResultService trainingResultService;

	public TrainingResultController(TrainingResultService trainingResultService) {
		this.trainingResultService = trainingResultService;
	}

	@GetMapping("/getResultByTrainingAssignmentId/{trainingAssignmentId}")
	public ResponseEntity getResultByTrainingAssignmentId(@PathVariable Long trainingAssignmentId) {

		return trainingResultService.getResultByTrainingAssignmentId(trainingAssignmentId);
	}
}
