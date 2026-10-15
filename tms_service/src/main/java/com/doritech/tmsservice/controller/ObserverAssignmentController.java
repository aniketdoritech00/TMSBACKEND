package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.ObserverAssignmentRequest;
import com.doritech.tmsservice.service.ObserverAssignmentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/observerAssignment")
public class ObserverAssignmentController {

	private final ObserverAssignmentService observerAssignmentService;

	public ObserverAssignmentController(ObserverAssignmentService observerAssignmentService) {
		this.observerAssignmentService = observerAssignmentService;
	}

	@PostMapping("/createObserverAssignment")
	public ResponseEntity createObserverAssignment(@RequestBody ObserverAssignmentRequest request) {
		return observerAssignmentService.createObserverAssignment(request);
	}
	
	@PutMapping("/updateObserverAssignment/{observerAssignmentId}")
	public ResponseEntity updateObserverAssignment(@PathVariable Long observerAssignmentId,
			@RequestBody ObserverAssignmentRequest request) {
		return observerAssignmentService.updateObserverAssignment(observerAssignmentId, request);
	}

	@GetMapping("/getAllObserverAssignment")
	public ResponseEntity getAllObserverAssignment() {
		return observerAssignmentService.getAllObserverAssignment();
	}

	@GetMapping("/getObserverAssignmentById/{observerAssignmentId}")
	public ResponseEntity getObserverAssignmentById(@PathVariable Long observerAssignmentId) {
		return observerAssignmentService.getObserverAssignmentById(observerAssignmentId);
	}

	@DeleteMapping("/deleteObserverAssignmentById/{observerAssignmentId}")
	public ResponseEntity deleteObserverAssignmentById(@PathVariable Long observerAssignmentId) {
		return observerAssignmentService.deleteObserverAssignmentById(observerAssignmentId);
	}
}
