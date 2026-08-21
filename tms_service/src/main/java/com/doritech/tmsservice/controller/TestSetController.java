package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TestSetRequest;
import com.doritech.tmsservice.service.TestSetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/test-sets")
public class TestSetController {

	private static final Logger log = LoggerFactory.getLogger(TestSetController.class);

	private final TestSetService testSetService;
	
	public TestSetController(TestSetService testSetService) {
		this.testSetService = testSetService;
	}

	@PostMapping("/createTestSet")
	public ResponseEntity createTestSet(@Valid @RequestBody TestSetRequest request) {
		log.info("createTestSet :: request received for name={}", request.getTestName());
		return testSetService.createTestSet(request);
	}

	@GetMapping("/getTestSetById/{id}")
	public ResponseEntity getTestSetById(@PathVariable("id") Long id) {
		log.info("getTestSetById :: request received for id={}", id);
		return testSetService.getTestSetById(id);
	}

	@GetMapping("/getAllTestSet")
	public ResponseEntity getAllTestSet(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "testSetId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllTestSet :: request received with page={}, size={}", page, size);
		return testSetService.getAllTestSet(page, size, sortBy, sortDir);
	}

	@GetMapping("/getTestSetsByTrainingId/{trainingId}")
	public ResponseEntity getTestSetsByTrainingId(@PathVariable("trainingId") Long trainingId) {
		log.info("getTestSetsByTrainingId :: request received for trainingId={}", trainingId);
		return testSetService.getTestSetsByTrainingId(trainingId);
	}

	@DeleteMapping("/deleteTestSet/{id}")
	public ResponseEntity deleteTestSet(@PathVariable("id") Long id) {
		log.info("deleteTestSet :: request received for id={}", id);
		return testSetService.deleteTestSet(id);
	}

	@PutMapping("/publishTestSet/{id}")
	public ResponseEntity publishTestSet(@PathVariable("id") Long id) {
		log.info("publishTestSet :: request received for id={}", id);
		return testSetService.publishTestSet(id);
	}
}