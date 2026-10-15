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

import com.doritech.tmsservice.request.TestAttemptRequest;
import com.doritech.tmsservice.service.TestAttemptService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/test-attempt")
public class TestAttemptController {

	private final TestAttemptService testAttemptService;

	public TestAttemptController(TestAttemptService testAttemptService) {
		this.testAttemptService = testAttemptService;
	}

	@PostMapping("/createTestAttempt")
	public ResponseEntity createTestAttempt(@RequestBody TestAttemptRequest request) {
		return testAttemptService.createTestAttempt(request);
	}

	@GetMapping("/getTestAttemptById/{testAttemptId}")
	public ResponseEntity getTestAttemptById(@PathVariable Long testAttemptId) {

		return testAttemptService.getTestAttemptById(testAttemptId);
	}

	@GetMapping("/getAllTestAttempts")
	public ResponseEntity getAllTestAttempts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "testAttemptId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return testAttemptService.getAllTestAttempts(page, size, sortBy, sortDir);
	}

	@GetMapping("/getTestAttemptsByUserId/{userId}")
	public ResponseEntity getTestAttemptsByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "testAttemptId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return testAttemptService.getTestAttemptsByUserId(userId, page, size, sortBy, sortDir);
	}

	@GetMapping("/getTestAttemptsByTestSetAndUser")
	public ResponseEntity getTestAttemptsByTestSetAndUser(@RequestParam Long testSetId, @RequestParam Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "testAttemptId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return testAttemptService.getTestAttemptsByTestSetAndUser(testSetId, userId, page, size, sortBy, sortDir);
	}

	@GetMapping("/getTestAttemptsByTrainingAssignment/{trainingAssignmentId}")
	public ResponseEntity getTestAttemptsByTrainingAssignment(@PathVariable Long trainingAssignmentId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "testAttemptId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return testAttemptService.getTestAttemptsByTrainingAssignment(trainingAssignmentId, page, size, sortBy,
				sortDir);
	}

	@GetMapping("/getUserTestAttemptsByTrainingAssignment")
	public ResponseEntity getUserTestAttemptsByTrainingAssignment(@RequestParam Long trainingAssignmentId,
			@RequestParam Long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "testAttemptId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return testAttemptService.getUserTestAttemptsByTrainingAssignment(trainingAssignmentId, userId, page, size,
				sortBy, sortDir);
	}

	@GetMapping("/getInProgressTestAttempt/{userId}")
	public ResponseEntity getInProgressTestAttempt(@PathVariable Long userId) {
		return testAttemptService.getInProgressTestAttempt(userId);
	}

	@PutMapping("/updateTestAttempt/{testAttemptId}")
	public ResponseEntity updateTestAttempt(@PathVariable Long testAttemptId, @RequestBody TestAttemptRequest request) {
		return testAttemptService.updateTestAttempt(testAttemptId, request);
	}

	@PutMapping("/completeTestAttempt/{testAttemptId}")
	public ResponseEntity completeTestAttempt(@PathVariable Long testAttemptId) {
		return testAttemptService.completeTestAttempt(testAttemptId);
	}

	@PutMapping("/abandonTestAttempt/{testAttemptId}")
	public ResponseEntity abandonTestAttempt(@PathVariable Long testAttemptId) {
		return testAttemptService.abandonTestAttempt(testAttemptId);
	}

	@PutMapping("/incrementViolationCount/{testAttemptId}")
	public ResponseEntity incrementViolationCount(@PathVariable Long testAttemptId) {
		return testAttemptService.incrementViolationCount(testAttemptId);
	}

	@DeleteMapping("/deleteTestAttempt/{testAttemptId}")
	public ResponseEntity deleteTestAttempt(@PathVariable Long testAttemptId) {
		return testAttemptService.deleteTestAttempt(testAttemptId);
	}

	@GetMapping("/getTestSetStatus")
	public ResponseEntity getTestSetStatus(@RequestParam Long testSetId) {
		return testAttemptService.getTestSetStatusByTestSetId(testSetId);
	}

	@GetMapping("/getByTestSetId/{testSetId}")
	public ResponseEntity getTestAttemptByTestSetId(@PathVariable Long testSetId) {
		return testAttemptService.getTestAttemptByTestSetId(testSetId);
	}
}