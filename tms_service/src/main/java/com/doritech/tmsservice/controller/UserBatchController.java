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

import com.doritech.tmsservice.request.UserBatchRequest;
import com.doritech.tmsservice.service.UserBatchService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/user-batch")
public class UserBatchController {

	private final UserBatchService userBatchService;

	public UserBatchController(UserBatchService userBatchService) {
		this.userBatchService = userBatchService;
	}

	@PostMapping("/assignBatch")
	public ResponseEntity assignBatch(@RequestBody UserBatchRequest request) {
		return userBatchService.assignBatchToUser(request);
	}

	@PutMapping("/updateUserBatch/{userBatchId}")
	public ResponseEntity updateUserBatch(@PathVariable Long userBatchId, @RequestBody UserBatchRequest request) {
		return userBatchService.updateUserBatch(userBatchId, request);
	}

	@GetMapping("/getAllUserBatches")
	public ResponseEntity getAllUserBatches(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return userBatchService.getAllUserBatches(page, size);
	}

	@GetMapping("/getUserBatchById/{userBatchId}")
	public ResponseEntity getUserBatchById(@PathVariable Long userBatchId) {
		return userBatchService.getUserBatchById(userBatchId);
	}

	@DeleteMapping("/deleteUserBatchById/{userBatchId}")
	public ResponseEntity deleteUserBatchById(@PathVariable Long userBatchId) {
		return userBatchService.deleteUserBatchById(userBatchId);
	}
}