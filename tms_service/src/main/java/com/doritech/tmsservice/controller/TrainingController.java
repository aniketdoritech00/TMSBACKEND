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

import com.doritech.tmsservice.request.TrainingRequest;
import com.doritech.tmsservice.service.TrainingService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/trainings")
public class TrainingController {

	private final TrainingService trainingService;

	public TrainingController(TrainingService trainingService) {
		this.trainingService = trainingService;
	}

	@PostMapping("/createTraining")
	public ResponseEntity createTraining(@Valid @RequestBody TrainingRequest request) {
		return trainingService.createTraining(request);
	}

	@GetMapping("/getTrainingById/{id}")
	public ResponseEntity getTrainingById(@PathVariable Long id) {
		return trainingService.getTrainingById(id);
	}

	@GetMapping("/getAllTraining")
	public ResponseEntity getAllTraining(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return trainingService.getAllTraining(page, size, sortBy, sortDir);
	}

	@GetMapping("/getAllTrainingWithoutPagination")
	public ResponseEntity getAllTrainingWithoutPagination() {
		return trainingService.getAllTraining();
	}

	@PutMapping("/updateTraining/{id}")
	public ResponseEntity updateTraining(@PathVariable Long id, @Valid @RequestBody TrainingRequest request) {
		return trainingService.updateTraining(id, request);
	}

	@DeleteMapping("/deleteTraining/{id}")
	public ResponseEntity deleteTraining(@PathVariable Long id) {
		return trainingService.deleteTraining(id);
	}

	@PutMapping("/publishTraining/{id}")
	public ResponseEntity publishTraining(@PathVariable Long id) {
		return trainingService.publishTraining(id);
	}
	
	@GetMapping("/getTrainingByCategoryId/{trainingCategoryId}")
	public ResponseEntity getTrainingByCategoryId(
			@PathVariable Long trainingCategoryId) {
		return trainingService.getTrainingByCategoryId(trainingCategoryId);
	}
}
