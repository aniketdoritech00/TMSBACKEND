package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.doritech.tmsservice.request.UserVideoRequest;
import com.doritech.tmsservice.service.UserVideoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/user-videos")
public class UserVideoController {

	private static final Logger log = LoggerFactory.getLogger(UserVideoController.class);

	private final UserVideoService userVideoService;

	public UserVideoController(UserVideoService userVideoService) {
		this.userVideoService = userVideoService;
	}

	@PostMapping("/createUserVideo")
	public ResponseEntity createUserVideo(@Valid @RequestBody UserVideoRequest request) {
		log.info("createUserVideo :: request received for userId={}, videoId={}", request.getUserId(),
				request.getVideoId());
		return userVideoService.createUserVideo(request);
	}

	@GetMapping("/getUserVideoById/{id}")
	public ResponseEntity getUserVideoById(@PathVariable("id") Long id) {
		log.info("getUserVideoById :: request received for id={}", id);
		return userVideoService.getUserVideoById(id);
	}

	@GetMapping("/getAllUserVideo")
	public ResponseEntity getAllUserVideo(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "userVideoId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllUserVideo :: request received with page={}, size={}", page, size);
		return userVideoService.getAllUserVideo(page, size, sortBy, sortDir);
	}

	@GetMapping("/getVideosByUserId/{userId}")
	public ResponseEntity getVideosByUserId(@PathVariable("userId") Long userId) {
		log.info("getVideosByUserId :: request received for userId={}", userId);
		return userVideoService.getVideosByUserId(userId);
	}

	@PutMapping("/markVideoWatched/{id}")
	public ResponseEntity markVideoWatched(@PathVariable("id") Long id) {
		log.info("markVideoWatched :: request received for id={}", id);
		return userVideoService.markVideoWatched(id);
	}

	@DeleteMapping("/deleteUserVideo/{id}")
	public ResponseEntity deleteUserVideo(@PathVariable("id") Long id) {
		log.info("deleteUserVideo :: request received for id={}", id);
		return userVideoService.deleteUserVideo(id);
	}
}