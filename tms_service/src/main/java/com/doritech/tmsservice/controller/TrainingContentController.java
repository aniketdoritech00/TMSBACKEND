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

import com.doritech.tmsservice.request.TrainingContentRequest;
import com.doritech.tmsservice.service.TrainingContentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/training-content")
public class TrainingContentController {

	private final TrainingContentService trainingContentService;

	public TrainingContentController(TrainingContentService trainingContentService) {
		this.trainingContentService = trainingContentService;
	}

	@PostMapping("/createTrainingContent")
	public ResponseEntity createTrainingContent(@RequestBody TrainingContentRequest request) {
		return trainingContentService.createTrainingContent(request);
	}

	@GetMapping("/getTrainingContentById/{id}")
	public ResponseEntity getTrainingContentById(@PathVariable Long id) {
		return trainingContentService.getTrainingContentById(id);
	}

	@GetMapping("/getAllTrainingContentWithoutPagination")
	public ResponseEntity getAllTrainingContent() {
		return trainingContentService.getAllTrainingContent();
	}

	@GetMapping("/getTrainingContentByTrainingId/{trainingId}")
	public ResponseEntity getTrainingContentByTrainingId(@PathVariable Long trainingId) {
		return trainingContentService.getTrainingContentByTrainingId(trainingId);
	}

	@PutMapping("/updateTrainingContent/{id}")
	public ResponseEntity updateTrainingContent(@PathVariable Long id, @RequestBody TrainingContentRequest request) {
		return trainingContentService.updateTrainingContent(id, request);
	}

	@DeleteMapping("/deleteTrainingContent/{id}")
	public ResponseEntity deleteTrainingContent(@PathVariable Long id) {
		return trainingContentService.deleteTrainingContent(id);
	}

	@GetMapping("/getAllTrainingContent")
	public ResponseEntity getAllTrainingContent(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingContentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return trainingContentService.getAllTrainingContent(page, size, sortBy, sortDir);
	}
}