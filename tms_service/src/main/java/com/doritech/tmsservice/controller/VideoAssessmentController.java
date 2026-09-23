package com.doritech.tmsservice.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoAssessmentRequest;
import com.doritech.tmsservice.service.VideoAssessmentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/tms/video-assessment")
public class VideoAssessmentController {

	private final VideoAssessmentService videoAssessmentService;

	public VideoAssessmentController(VideoAssessmentService videoAssessmentService) {
		this.videoAssessmentService = videoAssessmentService;
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity uploadVideoAssessment(@RequestParam("trainingId") Long trainingId,
			@RequestParam("trainingAssignmentId") Long trainingAssignmentId,
			@RequestPart("videoFile") MultipartFile videoFile) {
		VideoAssessmentRequest request = new VideoAssessmentRequest();
		request.setTrainingId(trainingId);
		request.setTrainingAssignmentId(trainingAssignmentId);
		return videoAssessmentService.uploadVideoAssessment(request, videoFile);
	}

	@GetMapping("/getMyVideoAssessments")
	public ResponseEntity getMyVideoAssessments(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "videoAssessmentId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return videoAssessmentService.getMyVideoAssessments(page, size, sortBy, sortDir);
	}

	@GetMapping("/getById/{videoAssessmentId}")
	public ResponseEntity getVideoAssessmentById(@PathVariable Long videoAssessmentId) {
		return videoAssessmentService.getVideoAssessmentById(videoAssessmentId);
	}

	@GetMapping("/getByTrainingAssignmentId/{trainingAssignmentId}")
	public ResponseEntity getByTrainingAssignmentId(@PathVariable Long trainingAssignmentId) {
		return videoAssessmentService.getByTrainingAssignmentId(trainingAssignmentId);
	}

	@GetMapping(value = "/streamVideo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void streamVideo(@RequestParam("videoId") Long videoAssessmentId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		videoAssessmentService.streamVideo(videoAssessmentId, request, response);
	}

	@GetMapping("/getStatusByTrainingAssignmentId/{trainingAssignmentId}")
	public ResponseEntity getStatusByTrainingAssignmentId(@PathVariable Long trainingAssignmentId) {
		return videoAssessmentService.getStatusByTrainingAssignmentId(trainingAssignmentId);
	}
}