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

import com.doritech.tmsservice.request.TestQuestionRequest;
import com.doritech.tmsservice.service.TestQuestionService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/test-questions")
public class TestQuestionController {

	private final TestQuestionService testQuestionService;

	public TestQuestionController(TestQuestionService testQuestionService) {
		this.testQuestionService = testQuestionService;
	}

	@PostMapping("/createTestQuestion")
	public ResponseEntity createTestQuestion(@RequestBody TestQuestionRequest request) {
		return testQuestionService.createTestQuestion(request);
	}

	@GetMapping("/getTestQuestionById/{id}")
	public ResponseEntity getTestQuestionById(@PathVariable Long id) {
		return testQuestionService.getTestQuestionById(id);
	}

	@GetMapping("/getAllTestQuestions/all")
	public ResponseEntity getAllTestQuestions() {
		return testQuestionService.getAllTestQuestions();
	}

	@GetMapping("/getTestQuestionsByTestSetId/{testSetId}")
	public ResponseEntity getTestQuestionsByTestSetId(@PathVariable Long testSetId) {
		return testQuestionService.getTestQuestionsByTestSetId(testSetId);
	}

	@PutMapping("/updateTestQuestion/{id}")
	public ResponseEntity updateTestQuestion(@PathVariable Long id, @RequestBody TestQuestionRequest request) {
		return testQuestionService.updateTestQuestion(id, request);
	}

	@DeleteMapping("/deleteTestQuestion/{id}")
	public ResponseEntity deleteTestQuestion(@PathVariable Long id) {
		return testQuestionService.deleteTestQuestion(id);
	}

	@GetMapping("/getAllTestQuestions/page")
	public ResponseEntity getAllTestQuestions(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "displayOrder") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return testQuestionService.getAllTestQuestions(page, size, sortBy, sortDir);
	}

	@GetMapping("/getTestQuestionsByTestSetId/{testSetId}/page")
	public ResponseEntity getTestQuestionsByTestSetId(@PathVariable Long testSetId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "displayOrder") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return testQuestionService.getTestQuestionsByTestSetId(testSetId, page, size, sortBy, sortDir);
	}
}