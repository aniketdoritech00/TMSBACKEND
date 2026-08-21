package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TestQuestionRequest;
import com.doritech.tmsservice.service.TestQuestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tmsService/api/test-questions")
public class TestQuestionController {

	private static final Logger log = LoggerFactory.getLogger(TestQuestionController.class);

	@Autowired
	private TestQuestionService testQuestionService;

	@PostMapping("/createTestQuestion")
	public ResponseEntity createTestQuestion(@Valid @RequestBody TestQuestionRequest request) {
		log.info("createTestQuestion :: request received for testSetId={}", request.getTestSetId());
		return testQuestionService.createTestQuestion(request);
	}

	@GetMapping("/getTestQuestionById/{id}")
	public ResponseEntity getTestQuestionById(@PathVariable("id") Long id) {
		log.info("getTestQuestionById :: request received for id={}", id);
		return testQuestionService.getTestQuestionById(id);
	}

	@GetMapping("/getAllTestQuestion")
	public ResponseEntity getAllTestQuestion(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "testQuestionId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllTestQuestion :: request received with page={}, size={}", page, size);
		return testQuestionService.getAllTestQuestion(page, size, sortBy, sortDir);
	}

	@GetMapping("/getQuestionsByTestSetId/{testSetId}")
	public ResponseEntity getQuestionsByTestSetId(@PathVariable("testSetId") Long testSetId) {
		log.info("getQuestionsByTestSetId :: request received for testSetId={}", testSetId);
		return testQuestionService.getQuestionsByTestSetId(testSetId);
	}

	@DeleteMapping("/deleteTestQuestion/{id}")
	public ResponseEntity deleteTestQuestion(@PathVariable("id") Long id) {
		log.info("deleteTestQuestion :: request received for id={}", id);
		return testQuestionService.deleteTestQuestion(id);
	}
}