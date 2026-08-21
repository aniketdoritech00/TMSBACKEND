package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.BatchRequest;
import com.doritech.tmsservice.service.BatchService;

@RestController
@RequestMapping("/api/tms/batch")
public class BatchController {

	private final BatchService batchService;

	public BatchController(BatchService batchService) {
		this.batchService = batchService;
	}

	@PostMapping("/createBatch")
	public ResponseEntity createBatch(@RequestBody BatchRequest request) {
		return batchService.createBatch(request);
	}

	@PutMapping("/updateBatch/{batchId}")
	public ResponseEntity updateBatch(@PathVariable Long batchId, @RequestBody BatchRequest request) {
		return batchService.updateBatch(batchId, request);
	}

	@GetMapping("/getAllBatch")
	public ResponseEntity getAllBatch() {
		return batchService.getAllBatch();

	}

	@GetMapping("/getBatchbyId/{batchId}")
	public ResponseEntity getBatchbyId(@PathVariable Long batchId) {
		return batchService.getBatchbyId(batchId);
	}

	@DeleteMapping("/deleteBatchbyId/{batchId}")
	public ResponseEntity deleteBatchbyId(@PathVariable Long batchId) {
		return batchService.deleteBatchbyId(batchId);
	}

}
