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

import com.doritech.tmsservice.request.TestSetRequest;
import com.doritech.tmsservice.service.TestSetService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/test-sets")
public class TestSetController {

	private final TestSetService testSetService;

	public TestSetController(TestSetService testSetService) {
		this.testSetService = testSetService;
	}

	@PostMapping("/createTestSet")
	public ResponseEntity createTestSet(@RequestBody TestSetRequest request) {
		return testSetService.createTestSet(request);
	}

	@GetMapping("/getTestSetById/{id}")
	public ResponseEntity getTestSetById(@PathVariable Long id) {
		return testSetService.getTestSetById(id);
	}

	@GetMapping("/getAllTestSets/all")
	public ResponseEntity getAllTestSets() {

		return testSetService.getAllTestSets();
	}

	@GetMapping("/getTestSetsByTrainingId/{trainingId}")
	public ResponseEntity getTestSetsByTrainingId(@PathVariable Long trainingId) {

		return testSetService.getTestSetsByTrainingId(trainingId);
	}

	@PutMapping("/updateTestSet/{id}")
	public ResponseEntity updateTestSet(@PathVariable Long id, @RequestBody TestSetRequest request) {
		return testSetService.updateTestSet(id, request);
	}
	
	@PutMapping("/publishTestSet/{id}")
	public ResponseEntity publishTestSet(@PathVariable Long id) {
	    return testSetService.publishTestSet(id);
	}


	@DeleteMapping("/deleteTestSet/{id}")
	public ResponseEntity deleteTestSet(@PathVariable Long id) {
		return testSetService.deleteTestSet(id);
	}

	@GetMapping("/getAllTestSets/page")
	public ResponseEntity getAllTestSets(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "testSetId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
		return testSetService.getAllTestSets(page, size, sortBy, sortDir);
	}

	@GetMapping("/getTestSetsByTrainingId/{trainingId}/page")
	public ResponseEntity getTestSetsByTrainingId(@PathVariable Long trainingId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "testSetId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
		return testSetService.getTestSetsByTrainingId(trainingId, page, size, sortBy, sortDir);
	}
}