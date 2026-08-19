package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.service.VideoSubProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tmsService/api/video-sub-products")
public class VideoSubProductController {

	private static final Logger log = LoggerFactory.getLogger(VideoSubProductController.class);

	@Autowired
	private VideoSubProductService videoSubProductService;

	@PostMapping("/assignVideoToSubProduct")
	public ResponseEntity assignVideoToSubProduct(@Valid @RequestBody VideoSubProductRequest request) {
		log.info("assignVideoToSubProduct :: request received for videoId={}, subProductId={}", request.getVideoId(),
				request.getSubProductId());
		return videoSubProductService.assignVideoToSubProduct(request);
	}

	@GetMapping("/getVideosBySubProductId/{subProductId}")
	public ResponseEntity getVideosBySubProductId(@PathVariable("subProductId") Long subProductId) {
		log.info("getVideosBySubProductId :: request received for subProductId={}", subProductId);
		return videoSubProductService.getVideosBySubProductId(subProductId);
	}

	@DeleteMapping("/removeVideoFromSubProduct")
	public ResponseEntity removeVideoFromSubProduct(@RequestParam("videoId") Long videoId,
			@RequestParam("subProductId") Long subProductId) {
		log.info("removeVideoFromSubProduct :: request received for videoId={}, subProductId={}", videoId,
				subProductId);
		return videoSubProductService.removeVideoFromSubProduct(videoId, subProductId);
	}
}