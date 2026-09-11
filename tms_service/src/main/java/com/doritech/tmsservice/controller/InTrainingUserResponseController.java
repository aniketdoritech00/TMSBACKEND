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

import com.doritech.tmsservice.request.InTrainingUserResponseRequest;
import com.doritech.tmsservice.service.InTrainingUserResponseService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/in-training-user-response")
public class InTrainingUserResponseController {

	private final InTrainingUserResponseService inTrainingUserResponseService;

	public InTrainingUserResponseController(InTrainingUserResponseService inTrainingUserResponseService) {
		this.inTrainingUserResponseService = inTrainingUserResponseService;
	}

	@PostMapping("/submitResponse")
	public ResponseEntity submitResponse(@RequestBody InTrainingUserResponseRequest request) {
		return inTrainingUserResponseService.submitResponse(request);
	}

	@GetMapping("/getResponseById/{inTrainingUserResponseId}")
	public ResponseEntity getResponseById(@PathVariable Long inTrainingUserResponseId) {
		return inTrainingUserResponseService.getResponseById(inTrainingUserResponseId);
	}

	@GetMapping("/getAllResponses")
	public ResponseEntity getAllResponses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "inTrainingUserResponseId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return inTrainingUserResponseService.getAllResponses(page, size, sortBy, sortDir);
	}

	@GetMapping("/getResponsesByTrainingAssignment/{trainingAssignmentId}")
	public ResponseEntity getResponsesByTrainingAssignment(@PathVariable Long trainingAssignmentId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "inTrainingUserResponseId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return inTrainingUserResponseService.getResponsesByTrainingAssignment(trainingAssignmentId, page, size, sortBy,
				sortDir);
	}

	@GetMapping("/getResponsesByQuestion/{questionId}")
	public ResponseEntity getResponsesByQuestion(@PathVariable Long questionId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "inTrainingUserResponseId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return inTrainingUserResponseService.getResponsesByQuestion(questionId, page, size, sortBy, sortDir);
	}

	@GetMapping("/getResponseByQuestionAndAssignment")
	public ResponseEntity getResponseByQuestionAndAssignment(@RequestParam Long questionId,
			@RequestParam Long trainingAssignmentId) {
		return inTrainingUserResponseService.getResponseByQuestionAndAssignment(questionId, trainingAssignmentId);
	}

	@PutMapping("/updateResponse/{inTrainingUserResponseId}")
	public ResponseEntity updateResponse(@PathVariable Long inTrainingUserResponseId,
			@RequestBody InTrainingUserResponseRequest request) {
		return inTrainingUserResponseService.updateResponse(inTrainingUserResponseId, request);
	}

	@DeleteMapping("/deleteResponse/{inTrainingUserResponseId}")
	public ResponseEntity deleteResponse(@PathVariable Long inTrainingUserResponseId) {
		return inTrainingUserResponseService.deleteResponse(inTrainingUserResponseId);
	}

	@DeleteMapping("/deleteResponsesByTrainingAssignment/{trainingAssignmentId}")
	public ResponseEntity deleteResponsesByTrainingAssignment(@PathVariable Long trainingAssignmentId) {
		return inTrainingUserResponseService.deleteResponsesByTrainingAssignment(trainingAssignmentId);
	}

	@GetMapping("/getCorrectAnswerCount/{trainingAssignmentId}")
	public ResponseEntity getCorrectAnswerCount(@PathVariable Long trainingAssignmentId) {
		return inTrainingUserResponseService.getCorrectAnswerCount(trainingAssignmentId);
	}

	@GetMapping("/getSkippedAnswerCount/{trainingAssignmentId}")
	public ResponseEntity getSkippedAnswerCount(@PathVariable Long trainingAssignmentId) {
		return inTrainingUserResponseService.getSkippedAnswerCount(trainingAssignmentId);
	}

	@GetMapping("/getResponseCount/{trainingAssignmentId}")
	public ResponseEntity getResponseCount(@PathVariable Long trainingAssignmentId) {
		return inTrainingUserResponseService.getResponseCount(trainingAssignmentId);
	}
}