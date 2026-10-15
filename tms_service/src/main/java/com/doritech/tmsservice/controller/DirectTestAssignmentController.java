package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@PostMapping("/createDirectTestAssignment")
	public ResponseEntity createDirectTestAssignment(@RequestBody DirectTestAssignmentRequest request) {
		return directTestAssignmentService.createDirectTestAssignment(request);
	}
}