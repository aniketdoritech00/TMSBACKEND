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

import com.doritech.tmsservice.request.BatchRequest;
import com.doritech.tmsservice.service.BatchService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

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
	public ResponseEntity getAllBatch(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return batchService.getAllBatch(page, size);
	}
    @GetMapping("/getAllBatchFilter")
    public ResponseEntity getAllBatch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String batchCode,
            @RequestParam(required = false) String batchName) {

        return batchService.getAllBatch(
                page,
                size,
                batchCode,
                batchName
        );
    }


	@GetMapping("/getBatchbyId/{batchId}")
	public ResponseEntity getBatchbyId(@PathVariable Long batchId) {
		return batchService.getBatchbyId(batchId);
	}

	@DeleteMapping("/deleteBatchbyId/{batchId}")
	public ResponseEntity deleteBatchbyId(@PathVariable Long batchId) {
		return batchService.deleteBatchbyId(batchId);
	}
	@GetMapping("/getallBatch")
	public ResponseEntity getallbatches() {
		return  batchService.getAllBatches();
		
	}
	

}
