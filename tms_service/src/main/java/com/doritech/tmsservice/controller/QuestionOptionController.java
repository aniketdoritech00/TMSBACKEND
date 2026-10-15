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

import com.doritech.tmsservice.request.QuestionOptionRequest;
import com.doritech.tmsservice.service.QuestionOptionService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/question-options")
public class QuestionOptionController {

	private final QuestionOptionService questionOptionService;

	public QuestionOptionController(QuestionOptionService questionOptionService) {
		this.questionOptionService = questionOptionService;
	}

	@PostMapping("/createQuestionOption")
	public ResponseEntity createQuestionOption(@RequestBody QuestionOptionRequest request) {
		return questionOptionService.createQuestionOption(request);
	}

	@GetMapping("/getQuestionOptionById/{id}")
	public ResponseEntity getQuestionOptionById(@PathVariable Long id) {
		return questionOptionService.getQuestionOptionById(id);
	}

	@GetMapping("/getAllQuestionOptions/all")
	public ResponseEntity getAllQuestionOptions() {
		return questionOptionService.getAllQuestionOptions();
	}

	@GetMapping("/getQuestionOptionsByTestQuestionId/{testQuestionId}")
	public ResponseEntity getQuestionOptionsByTestQuestionId(@PathVariable Long testQuestionId) {
		return questionOptionService.getQuestionOptionsByTestQuestionId(testQuestionId);
	}

	@PutMapping("/updateQuestionOption/{id}")
	public ResponseEntity updateQuestionOption(@PathVariable Long id, @RequestBody QuestionOptionRequest request) {
		return questionOptionService.updateQuestionOption(id, request);
	}

	@DeleteMapping("/deleteQuestionOption/{id}")
	public ResponseEntity deleteQuestionOption(@PathVariable Long id) {
		return questionOptionService.deleteQuestionOption(id);
	}

	@GetMapping("/getAllQuestionOptions/page")
	public ResponseEntity getAllQuestionOptions(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "displayOrder") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return questionOptionService.getAllQuestionOptions(page, size, sortBy, sortDir);
	}

	@GetMapping("/getQuestionOptionsByTestQuestionId/{testQuestionId}/page")
	public ResponseEntity getQuestionOptionsByTestQuestionId(@PathVariable Long testQuestionId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "displayOrder") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return questionOptionService.getQuestionOptionsByTestQuestionId(testQuestionId, page, size, sortBy, sortDir);
	}
}