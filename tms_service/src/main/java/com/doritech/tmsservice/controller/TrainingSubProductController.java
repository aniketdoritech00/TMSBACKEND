package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.TrainingSubProductRequest;
import com.doritech.tmsservice.service.TrainingSubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/training-sub-products")
public class TrainingSubProductController {

	private final TrainingSubProductService trainingSubProductService;

	public TrainingSubProductController(TrainingSubProductService trainingSubProductService) {
		this.trainingSubProductService = trainingSubProductService;
	}

	@PostMapping("/assignSubProduct")
	public ResponseEntity assignSubProduct(@Valid @RequestBody TrainingSubProductRequest request) {
		return trainingSubProductService.assignSubProductToTraining(request);
	}

	@GetMapping("/getSubProductsByTrainingId/{trainingId}")
	public ResponseEntity getSubProductsByTrainingId(@PathVariable Long trainingId) {
		return trainingSubProductService.getSubProductsByTrainingId(trainingId);
	}

	@GetMapping("/getTrainingsBySubProductId/{subProductId}")
	public ResponseEntity getTrainingsBySubProductId(@PathVariable Long subProductId) {
		return trainingSubProductService.getTrainingsBySubProductId(subProductId);
	}

	@DeleteMapping("/removeSubProduct/{trainingId}/{subProductId}")
	public ResponseEntity removeSubProduct(@PathVariable Long trainingId, @PathVariable Long subProductId) {
		return trainingSubProductService.deleteTrainingSubProduct(trainingId, subProductId);
	}
}