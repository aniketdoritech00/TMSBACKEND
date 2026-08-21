package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingAssignmentRequest;
import com.doritech.tmsservice.service.TrainingAssignmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/training-assignments")
public class TrainingAssignmentController {

	private static final Logger log = LoggerFactory.getLogger(TrainingAssignmentController.class);

	private final TrainingAssignmentService trainingAssignmentService;

	public TrainingAssignmentController(TrainingAssignmentService trainingAssignmentService) {
		this.trainingAssignmentService = trainingAssignmentService;
	}

	@PostMapping("/createTrainingAssignment")
	public ResponseEntity createTrainingAssignment(@Valid @RequestBody TrainingAssignmentRequest request) {
		log.info("createTrainingAssignment :: request received for trainingId={}, userId={}", request.getTrainingId(),
				request.getUserId());
		return trainingAssignmentService.createTrainingAssignment(request);
	}

	@GetMapping("/getTrainingAssignmentById/{id}")
	public ResponseEntity getTrainingAssignmentById(@PathVariable("id") Long id) {
		log.info("getTrainingAssignmentById :: request received for id={}", id);
		return trainingAssignmentService.getTrainingAssignmentById(id);
	}

	@GetMapping("/getAllTrainingAssignment")
	public ResponseEntity getAllTrainingAssignment(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingAssignmentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllTrainingAssignment :: request received with page={}, size={}", page, size);
		return trainingAssignmentService.getAllTrainingAssignment(page, size, sortBy, sortDir);
	}

	@GetMapping("/getAssignmentsByUserId/{userId}")
	public ResponseEntity getAssignmentsByUserId(@PathVariable("userId") Long userId) {
		log.info("getAssignmentsByUserId :: request received for userId={}", userId);
		return trainingAssignmentService.getAssignmentsByUserId(userId);
	}

	@GetMapping("/getAssignmentsByTrainingId/{trainingId}")
	public ResponseEntity getAssignmentsByTrainingId(@PathVariable("trainingId") Long trainingId) {
		log.info("getAssignmentsByTrainingId :: request received for trainingId={}", trainingId);
		return trainingAssignmentService.getAssignmentsByTrainingId(trainingId);
	}

	@PutMapping("/startTrainingAssignment/{id}")
	public ResponseEntity startTrainingAssignment(@PathVariable("id") Long id) {
		log.info("startTrainingAssignment :: request received for id={}", id);
		return trainingAssignmentService.startTrainingAssignment(id);
	}

	@DeleteMapping("/deleteTrainingAssignment/{id}")
	public ResponseEntity deleteTrainingAssignment(@PathVariable("id") Long id) {
		log.info("deleteTrainingAssignment :: request received for id={}", id);
		return trainingAssignmentService.deleteTrainingAssignment(id);
	}
}