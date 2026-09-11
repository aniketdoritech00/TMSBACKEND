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

import com.doritech.tmsservice.request.UserVideoRequest;
import com.doritech.tmsservice.service.UserVideoService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/user-videos")
public class UserVideoController {

	private final UserVideoService userVideoService;

	public UserVideoController(UserVideoService userVideoService) {
		this.userVideoService = userVideoService;
	}

	@PostMapping("/createUserVideo")
	public ResponseEntity createUserVideo(@Valid @RequestBody UserVideoRequest request) {
		return userVideoService.createUserVideo(request);
	}

	@GetMapping("/getUserVideoById/{id}")
	public ResponseEntity getUserVideoById(@PathVariable Long id) {
		return userVideoService.getUserVideoById(id);
	}

	@GetMapping("/getAllUserVideos/all")
	public ResponseEntity getAllUserVideos() {
		return userVideoService.getAllUserVideos();
	}

	@GetMapping("/getUserVideosByUserId/{userId}")
	public ResponseEntity getUserVideosByUserId(@PathVariable Long userId) {
		return userVideoService.getUserVideosByUserId(userId);

	}

	@GetMapping("/getUserVideosByTrainingAssignmentId/{trainingAssignmentId}")
	public ResponseEntity getUserVideosByTrainingAssignmentId(@PathVariable Long trainingAssignmentId) {
		return userVideoService.getUserVideosByTrainingAssignmentId(trainingAssignmentId);

	}

	@PutMapping("/updateWatchStatus/{id}")
	public ResponseEntity updateWatchStatus(@PathVariable Long id) {
		return userVideoService.updateWatchStatus(id);
	}

	@PutMapping("/updateWatchProgress/{id}")
	public ResponseEntity updateWatchProgress(@PathVariable Long id, @RequestParam Integer watchedSeconds) {
		return userVideoService.updateWatchProgress(id, watchedSeconds);

	}

	@GetMapping("/getVideoCompletionStatus/{id}")
	public ResponseEntity getVideoCompletionStatus(@PathVariable Long id) {
		return userVideoService.getVideoCompletionStatus(id);

	}

	@DeleteMapping("/deleteUserVideo/{id}")
	public ResponseEntity deleteUserVideo(@PathVariable Long id) {

		return userVideoService.deleteUserVideo(id);
	}

	@GetMapping("/getAllUserVideos/page")
	public ResponseEntity getAllUserVideos(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "userVideoId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return userVideoService.getAllUserVideos(page, size, sortBy, sortDir);

	}

	@GetMapping("/getUserVideosByUserId/{userId}/page")
	public ResponseEntity getUserVideosByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "userVideoId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return userVideoService.getUserVideosByUserId(userId, page, size, sortBy, sortDir);

	}

	@GetMapping("/getUserVideosByTrainingAssignmentId/{trainingAssignmentId}/page")
	public ResponseEntity getUserVideosByTrainingAssignmentId(@PathVariable Long trainingAssignmentId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "userVideoId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return userVideoService.getUserVideosByTrainingAssignmentId(trainingAssignmentId, page, size, sortBy, sortDir);
	}
}