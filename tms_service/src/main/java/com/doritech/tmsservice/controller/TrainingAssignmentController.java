package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.TrainingAssignmentRequest;
import com.doritech.tmsservice.service.TrainingAssignmentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/training-assignments")
public class TrainingAssignmentController {

	private final TrainingAssignmentService trainingAssignmentService;

	public TrainingAssignmentController(TrainingAssignmentService trainingAssignmentService) {
		this.trainingAssignmentService = trainingAssignmentService;
	}

	@PostMapping("/createTrainingAssignment")
	public ResponseEntity createTrainingAssignment(@RequestBody TrainingAssignmentRequest request) {
		return trainingAssignmentService.createTrainingAssignment(request);
	}

	@GetMapping("/getTrainingAssignmentById/{id}")
	public ResponseEntity getTrainingAssignmentById(@PathVariable Long id) {
		return trainingAssignmentService.getTrainingAssignmentById(id);
	}

	@GetMapping("/getAllTrainingAssignments/all")
	public ResponseEntity getAllTrainingAssignments() {
		return trainingAssignmentService.getAllTrainingAssignments();
	}

	@GetMapping("/getTrainingAssignmentsByTrainingId/{trainingId}")
	public ResponseEntity getTrainingAssignmentsByTrainingId(@PathVariable Long trainingId) {
		return trainingAssignmentService.getTrainingAssignmentsByTrainingId(trainingId);
	}

	@GetMapping("/getTrainingAssignmentsByUserId/{userId}")
	public ResponseEntity getTrainingAssignmentsByUserId(@PathVariable Long userId) {
		return trainingAssignmentService.getTrainingAssignmentsByUserId(userId);
	}

	@GetMapping("/getTrainingAssignmentsForCurrentUser")
	public ResponseEntity getTrainingAssignmentsForCurrentUser() {
		return trainingAssignmentService.getTrainingAssignmentsForCurrentUser();
	}

	@PutMapping("/updateTrainingAssignment/{id}")
	public ResponseEntity updateTrainingAssignment(@PathVariable Long id,
			@RequestBody TrainingAssignmentRequest request) {
		return trainingAssignmentService.updateTrainingAssignment(id, request);
	}

	@DeleteMapping("/deleteTrainingAssignment/{id}")
	public ResponseEntity deleteTrainingAssignment(@PathVariable Long id) {
		return trainingAssignmentService.deleteTrainingAssignment(id);
	}

	@GetMapping("/getAllTrainingAssignments/page")
	public ResponseEntity getAllTrainingAssignments(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingAssignmentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
		return trainingAssignmentService.getAllTrainingAssignments(page, size, sortBy, sortDir);
	}

	@GetMapping("/getTrainingAssignmentsByTrainingId/{trainingId}/page")
	public ResponseEntity getTrainingAssignmentsByTrainingId(@PathVariable Long trainingId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingAssignmentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
		return trainingAssignmentService.getTrainingAssignmentsByTrainingId(trainingId, page, size, sortBy, sortDir);
	}

	@GetMapping("/getTrainingAssignmentsByUserId/{userId}/page")
	public ResponseEntity getTrainingAssignmentsByUserId(@PathVariable Long userId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingAssignmentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

		return trainingAssignmentService.getTrainingAssignmentsByUserId(userId, page, size, sortBy, sortDir);
	}

	@GetMapping("/getTrainingAssignmentsForCurrentUser/page")
	public ResponseEntity getTrainingAssignmentsForCurrentUser(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingAssignmentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
		return trainingAssignmentService.getTrainingAssignmentsForCurrentUser(page, size, sortBy, sortDir);
	}

	@PutMapping("/updateProgress/{trainingAssignmentId}")
	public ResponseEntity updateTrainingAssignmentProgress(@PathVariable Long trainingAssignmentId) {
		return trainingAssignmentService.updateTrainingAssignmentProgress(trainingAssignmentId);
	}
}
