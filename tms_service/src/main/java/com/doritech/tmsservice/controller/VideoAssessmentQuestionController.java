package com.doritech.tmsservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.VideoAssessmentQuestionRequest;
import com.doritech.tmsservice.service.VideoAssessmentQuestionService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/video-assessment-question")
public class VideoAssessmentQuestionController {

	private final VideoAssessmentQuestionService videoAssessmentQuestionService;

	public VideoAssessmentQuestionController(VideoAssessmentQuestionService videoAssessmentQuestionService) {
		this.videoAssessmentQuestionService = videoAssessmentQuestionService;

	}

	@PostMapping("/create")
	public ResponseEntity createVideoAssessmentQuestion(@RequestBody List<VideoAssessmentQuestionRequest> requests) {

		return videoAssessmentQuestionService.createVideoAssessmentQuestion(requests);
	}

	@GetMapping("/getById/{videoAssessmentQuestionId}")
	public ResponseEntity getVideoAssessmentQuestionById(@PathVariable Long videoAssessmentQuestionId) {
		return videoAssessmentQuestionService.getVideoAssessmentQuestionById(videoAssessmentQuestionId);
	}

	@GetMapping("/getAll")
	public ResponseEntity getAllVideoAssessmentQuestions(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "videoAssessmentQuestionId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return videoAssessmentQuestionService.getAllVideoAssessmentQuestions(page, size, sortBy, sortDir);
	}

	@GetMapping("/getByTrainingId/{trainingId}")
	public ResponseEntity getVideoAssessmentQuestionsByTrainingId(@PathVariable Long trainingId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "videoAssessmentQuestionId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return videoAssessmentQuestionService.getVideoAssessmentQuestionsByTrainingId(trainingId, page, size, sortBy,
				sortDir);
	}

	@GetMapping("/getByTrainingIdWithoutPagination/{trainingId}")
	public ResponseEntity getByTrainingIdWithoutPagination(@PathVariable Long trainingId) {
		return videoAssessmentQuestionService.getVideoAssessmentQuestionsByTrainingId(trainingId);
	}

	@PutMapping("/update/{videoAssessmentQuestionId}")
	public ResponseEntity updateVideoAssessmentQuestion(@PathVariable Long videoAssessmentQuestionId,
			@RequestBody VideoAssessmentQuestionRequest request) {
		return videoAssessmentQuestionService.updateVideoAssessmentQuestion(videoAssessmentQuestionId, request);
	}

	@DeleteMapping("/delete/{videoAssessmentQuestionId}")
	public ResponseEntity deleteVideoAssessmentQuestion(@PathVariable Long videoAssessmentQuestionId) {
		return videoAssessmentQuestionService.deleteVideoAssessmentQuestion(videoAssessmentQuestionId);
	}

	@GetMapping("/getAllWithoutPagination")
	public ResponseEntity getAllVideoAssessmentQuestionsWithoutPagination() {
		return videoAssessmentQuestionService.getAllVideoAssessmentQuestionsWithoutPaginatioan();
	}

}