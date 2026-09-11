package com.doritech.tmsservice.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.request.VideoUpdateRequest;
import com.doritech.tmsservice.service.VideoSubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/video-sub-products")
public class VideoSubProductController {

	private final VideoSubProductService videoSubProductService;

	public VideoSubProductController(VideoSubProductService videoSubProductService) {
		this.videoSubProductService = videoSubProductService;
	}

	@PostMapping("/assignVideoToSubProduct")
	public ResponseEntity assignVideoToSubProduct(@Valid @RequestBody VideoSubProductRequest request) {
		return videoSubProductService.assignVideoToSubProduct(request);
	}

	@GetMapping("/getVideosBySubProductId/{subProductId}")
	public ResponseEntity getVideosBySubProductId(@PathVariable Long subProductId) {
		return videoSubProductService.getVideosBySubProductId(subProductId);
	}

	@DeleteMapping("/removeVideoFromSubProduct")
	public ResponseEntity removeVideoFromSubProduct(@RequestParam Long videoId, @RequestParam Long subProductId) {
		return videoSubProductService.removeVideoFromSubProduct(videoId, subProductId);
	}

	@PostMapping(value = "/uploadVideoAndThumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity uploadVideAndThumbnail(@Valid @RequestPart("video") VideoRequest request,
			@RequestPart("videoFile") MultipartFile videoFile,
			@RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
			@RequestParam("subProductIds") List<Long> subProductIds) {
		return videoSubProductService.uploadVideAndThumbnail(request, videoFile, thumbnailFile, subProductIds);
	}

	@PutMapping("/updateVideo/{videoId}")
	public ResponseEntity updateVideo(@PathVariable("videoId") Long videoId,
			@Valid @RequestBody VideoUpdateRequest request) {
		return videoSubProductService.updateVideo(videoId, request);
	}
}