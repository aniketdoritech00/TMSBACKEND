package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.TrainingCategoryRequest;
import com.doritech.tmsservice.service.TrainingCategoryService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/training-categories")
public class TrainingCategoryController {

	private final TrainingCategoryService trainingCategoryService;

	public TrainingCategoryController(TrainingCategoryService trainingCategoryService) {
		this.trainingCategoryService = trainingCategoryService;
	}

	@PostMapping("/createTrainingCategory")
	public ResponseEntity createTrainingCategory(@Valid @RequestBody TrainingCategoryRequest request) {
		return trainingCategoryService.createTrainingCategory(request);
	}

	@GetMapping("/getTrainingCategoryById/{id}")
	public ResponseEntity getTrainingCategoryById(@PathVariable("id") Long id) {
		return trainingCategoryService.getTrainingCategoryById(id);
	}

	@GetMapping("/getAllTrainingCategory")
	public ResponseEntity getAllTrainingCategory(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingCategoryId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return trainingCategoryService.getAllTrainingCategory(page, size, sortBy, sortDir);
	}

	@DeleteMapping("/deleteTrainingCategory/{id}")
	public ResponseEntity deleteTrainingCategory(@PathVariable("id") Long id) {
		return trainingCategoryService.deleteTrainingCategory(id);
	}
	
	@GetMapping("/getAllTrainingCategoryWithoutPagination")
	public ResponseEntity getAllTrainingCategoryWithoutPagination() {
		return trainingCategoryService.getAllTrainingCategory();
	}
}