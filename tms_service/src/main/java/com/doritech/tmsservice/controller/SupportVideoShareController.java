package com.doritech.tmsservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.response.SupportVideoShareResponse;
import com.doritech.tmsservice.service.SupportVideoShareService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/support-video-share")
public class SupportVideoShareController {

	private final SupportVideoShareService supportVideoShareService;

	public SupportVideoShareController(SupportVideoShareService supportVideoShareService) {

		this.supportVideoShareService = supportVideoShareService;
	}

	@PostMapping("/share/{videoId}")
	public ResponseEntity shareVideo(@PathVariable Long videoId) {

		Long currentUserId = getCurrentUserId();

		SupportVideoShareResponse response = supportVideoShareService.shareVideo(videoId, currentUserId);

		return new ResponseEntity("Video shared successfully", HttpStatus.OK.value(), response);
	}

	@GetMapping("/{shareToken}")
	public ResponseEntity getSharedVideo(@PathVariable String shareToken) {

		Long currentUserId = getCurrentUserId();

		SupportVideoShareResponse response = supportVideoShareService.getSharedVideoByToken(shareToken, currentUserId);

		return new ResponseEntity("Shared video retrieved successfully", HttpStatus.OK.value(), response);
	}

	@PutMapping("/{shareToken}/viewed")
	public ResponseEntity markVideoViewed(@PathVariable String shareToken) {

		Long currentUserId = getCurrentUserId();

		supportVideoShareService.markVideoViewed(shareToken, currentUserId);

		return new ResponseEntity("Video marked as viewed", HttpStatus.OK.value(), null);
	}

	@PutMapping("/{shareToken}/completed")
	public ResponseEntity markVideoCompleted(@PathVariable String shareToken) {

		Long currentUserId = getCurrentUserId();

		supportVideoShareService.markVideoCompleted(shareToken, currentUserId);

		return new ResponseEntity("Video marked as completed", HttpStatus.OK.value(), null);
	}

	private Long getCurrentUserId() {
		return 47L;
	}
}