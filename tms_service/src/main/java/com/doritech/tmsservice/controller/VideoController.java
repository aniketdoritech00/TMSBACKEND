package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.service.VideoService;

@RestController
@RequestMapping("/api/tms/videos")
public class VideoController {

	private static final Logger log = LoggerFactory.getLogger(VideoController.class);

	private final VideoService videoService;

	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}

	@PostMapping(value = "/createVideo", consumes = "multipart/form-data")
	public ResponseEntity createVideo(@RequestParam("file") MultipartFile file,
			@RequestParam("videoTitle") String videoTitle,
			@RequestParam(value = "videoDescription", required = false) String videoDescription,
			@RequestParam(value = "thumbnailUrl", required = false) String thumbnailUrl,
			@RequestParam(value = "durationSeconds", required = false) Integer durationSeconds,
			@RequestParam(value = "videoFormat", required = false) String videoFormat,
			@RequestParam(value = "resolution", required = false) String resolution,
			@RequestParam(value = "isSecure", required = false) Boolean isSecure,
			@RequestParam(value = "allowDownload", required = false) Boolean allowDownload,
			@RequestParam(value = "allowScreenRecord", required = false) Boolean allowScreenRecord,
			@RequestParam(value = "allowScreenshot", required = false) Boolean allowScreenshot,
			@RequestParam("status") String status, @RequestParam("uploadedBy") Long uploadedBy) {

		log.info("createVideo :: request received for title={}", videoTitle);

		VideoRequest request = new VideoRequest();
		request.setVideoTitle(videoTitle);
		request.setVideoDescription(videoDescription);
		request.setThumbnailUrl(thumbnailUrl);
		request.setDurationSeconds(durationSeconds);
		request.setVideoFormat(videoFormat);
		request.setResolution(resolution);
		request.setIsSecure(isSecure);
		request.setAllowDownload(allowDownload);
		request.setAllowScreenRecord(allowScreenRecord);
		request.setAllowScreenshot(allowScreenshot);
		request.setStatus(status);
		request.setUploadedBy(uploadedBy);

		return videoService.createVideo(request, file);
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

	@DeleteMapping("/deleteVideo/{id}")
	public ResponseEntity deleteVideo(@PathVariable("id") Long id) {
		log.info("deleteVideo :: request received for id={}", id);
		return videoService.deleteVideo(id);
	}
}