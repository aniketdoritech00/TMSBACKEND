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

import com.doritech.tmsservice.request.VideoAccessControlRequest;
import com.doritech.tmsservice.service.VideoAccessControlService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/video-access-control")
public class VideoAccessControlController {

	private final VideoAccessControlService videoAccessControlService;

	public VideoAccessControlController(VideoAccessControlService videoAccessControlService) {
		this.videoAccessControlService = videoAccessControlService;
	}

	@PostMapping("/createVideoAccessControl")
	public ResponseEntity createVideoAccessControl(@RequestBody VideoAccessControlRequest request) {
		return videoAccessControlService.createVideoAccessControl(request);
	}

	@GetMapping("/getVideoAccessControlById/{id}")
	public ResponseEntity getVideoAccessControlById(@PathVariable Long id) {
		return videoAccessControlService.getVideoAccessControlById(id);
	}

	@GetMapping("/getAllVideoAccessControl")
	public ResponseEntity getAllVideoAccessControl(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "videoAccessId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return videoAccessControlService.getAllVideoAccessControl(page, size, sortBy, sortDir);
	}

	@GetMapping("/getAccessByUserId/{userId}")
	public ResponseEntity getAccessByUserId(@PathVariable Long userId) {
		return videoAccessControlService.getAccessByUserId(userId);
	}

	@GetMapping("/checkAccess")
	public ResponseEntity checkAccess(@RequestParam Long videoId, @RequestParam Long userId) {
		return videoAccessControlService.checkAccess(videoId, userId);
	}

	@PutMapping("/revokeAccess/{id}")
	public ResponseEntity revokeAccess(@PathVariable Long id) {
		return videoAccessControlService.revokeAccess(id);
	}

	@DeleteMapping("/deleteVideoAccessControl/{id}")
	public ResponseEntity deleteVideoAccessControl(@PathVariable Long id) {
		return videoAccessControlService.deleteVideoAccessControl(id);
	}
}