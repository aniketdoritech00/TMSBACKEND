package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.service.VideoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tmsService/api/videos")
public class VideoController {

	private static final Logger log = LoggerFactory.getLogger(VideoController.class);

	@Autowired
	private VideoService videoService;

	@PostMapping("/createVideo")
	public ResponseEntity createVideo(@Valid @RequestBody VideoRequest videoRequest) {
		log.info("createVideo :: request received for title={}", videoRequest.getVideoTitle());
		return videoService.createVideo(videoRequest);
	}

	@GetMapping("/getVideoById/{id}")
	public ResponseEntity getVideoById(@PathVariable("id") Long id) {
		log.info("getVideoById :: request received for id={}", id);
		return videoService.getVideoById(id);
	}

	@GetMapping("/getAllVideo")
	public ResponseEntity getAllVideo(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "videoId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllVideo :: request received with page={}, size={}", page, size);
		return videoService.getAllVideo(page, size, sortBy, sortDir);
	}
}