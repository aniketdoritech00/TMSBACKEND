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

import com.doritech.tmsservice.request.DirectTestAssignmentRequest;
import com.doritech.tmsservice.service.DirectTestAssignmentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/direct-test-assignments")
public class DirectTestAssignmentController {

	private final DirectTestAssignmentService directTestAssignmentService;

	public DirectTestAssignmentController(DirectTestAssignmentService directTestAssignmentService) {
		this.directTestAssignmentService = directTestAssignmentService;
	}

	// ================= Create Assignment =======================
	@PostMapping("/createDirectTestAssignment")
	public ResponseEntity createDirectTestAssignment(@RequestBody DirectTestAssignmentRequest request) {
		return directTestAssignmentService.createDirectTestAssignment(request);
	}

	// ==================== Get all with current user ===================
	@GetMapping("/get-all-testset-current-user")
	public ResponseEntity getMyDirectTestSetsForCurrentUser() {
		return directTestAssignmentService.getMyDirectTestSets();
	}

	// ==================== GET All Direct_Test_Sets ===============
	@GetMapping("/get-all-direct-test")
	public ResponseEntity getAllDirectTestSet(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return directTestAssignmentService.getAllDirectTestSet(page, size);
	}

	// =================== Get direct Test By id=======================
	@GetMapping("/get-direct-test-byId/{id}")
	public ResponseEntity getDirectTestSetsById(@PathVariable Long id) {
		return directTestAssignmentService.getDirectTestSetsById(id);
	}

	// ================== Delete direct test set by Id =====================
	@DeleteMapping("/delete-by-id/{id}")
	public ResponseEntity deleteById(@PathVariable Long id) {
		return directTestAssignmentService.deleteById(id);
	}

	// ================== update direct test set assignment API ===================
	@PutMapping("/update/{id}")
	public ResponseEntity updateDirectTestAssignment(@PathVariable Long id,
			@RequestBody DirectTestAssignmentRequest request) {
		return directTestAssignmentService.updateDirectTestAssignment(id, request);
	}
}