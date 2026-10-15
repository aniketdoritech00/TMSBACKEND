package com.doritech.tmsservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.InTrainingQuestionRequest;
import com.doritech.tmsservice.service.InTrainingQuestionService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/in-training-questions")
public class InTrainingQuestionController {

	private final InTrainingQuestionService inTrainingQuestionService;

	public InTrainingQuestionController(InTrainingQuestionService inTrainingQuestionService) {
		this.inTrainingQuestionService = inTrainingQuestionService;
	}

//	@PostMapping("/createInTrainingQuestion")
//	public ResponseEntity createInTrainingQuestion(@RequestBody InTrainingQuestionRequest request) {
//		return inTrainingQuestionService.createInTrainingQuestion(request);
//	}

	@PostMapping("/createInTrainingQuestion")
	public ResponseEntity createInTrainingQuestion(@RequestBody List<InTrainingQuestionRequest> requests) {
		return inTrainingQuestionService.createInTrainingQuestion(requests);
	}

	@GetMapping("/getInTrainingQuestionById/{id}")
	public ResponseEntity getInTrainingQuestionById(@PathVariable Long id) {
		return inTrainingQuestionService.getInTrainingQuestionById(id);
	}

	@GetMapping("/getAllInTrainingQuestions")
	public ResponseEntity getAllInTrainingQuestions() {

		return inTrainingQuestionService.getAllInTrainingQuestions();
	}

	@GetMapping("/getInTrainingQuestionsByVideoId/{videoId}")
	public ResponseEntity getInTrainingQuestionsByVideoId(@PathVariable Long videoId) {
		return inTrainingQuestionService.getInTrainingQuestionsByVideoId(videoId);
	}

	@GetMapping("/getInTrainingQuestionsByVideoIdByVideoIdAndTrainingAssinmentId")
	public ResponseEntity getInTrainingQuestionsByVideoId(@RequestParam Long videoId,
			@RequestParam Long trainingAssignmentId) {
		return inTrainingQuestionService.getInTrainingQuestionsByVideoId(videoId, trainingAssignmentId);
	}

	@PutMapping("/updateInTrainingQuestion/{id}")
	public ResponseEntity updateInTrainingQuestion(@PathVariable Long id,
			@RequestBody InTrainingQuestionRequest request) {
		return inTrainingQuestionService.updateInTrainingQuestion(id, request);
	}

	@DeleteMapping("/deleteInTrainingQuestion/{id}")
	public ResponseEntity deleteInTrainingQuestion(@PathVariable Long id) {
		return inTrainingQuestionService.deleteInTrainingQuestion(id);
	}

	@GetMapping("/getAllInTrainingQuestions/page")
	public ResponseEntity getAllInTrainingQuestions(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "displayOrder") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

		return inTrainingQuestionService.getAllInTrainingQuestions(page, size, sortBy, sortDir);
	}

	@GetMapping("/getInTrainingQuestionsByVideoId/{videoId}/page")
	public ResponseEntity getInTrainingQuestionsByVideoId(@PathVariable Long videoId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "displayOrder") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return inTrainingQuestionService.getInTrainingQuestionsByVideoId(videoId, page, size, sortBy, sortDir);
	}

	@GetMapping("/getInTrainingResultByVideoIdAndTrainingAssignmentId")
	public ResponseEntity getInTrainingResultByVideoId(@RequestParam Long videoId,
			@RequestParam Long trainingAssignmentId) {
		return inTrainingQuestionService.getInTrainingResultByVideoId(videoId, trainingAssignmentId);
	}
}