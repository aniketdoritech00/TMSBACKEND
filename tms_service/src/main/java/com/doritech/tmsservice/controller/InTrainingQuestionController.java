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
import com.doritech.tmsservice.request.InTrainingQuestionRequest;
import com.doritech.tmsservice.service.InTrainingQuestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/in-training-questions")
public class InTrainingQuestionController {

	private static final Logger log = LoggerFactory.getLogger(InTrainingQuestionController.class);
	
	private final InTrainingQuestionService inTrainingQuestionService;
	
	public InTrainingQuestionController(InTrainingQuestionService inTrainingQuestionService) {
		this.inTrainingQuestionService = inTrainingQuestionService;
		
	}

	@PostMapping("/createInTrainingQuestion")
	public ResponseEntity createInTrainingQuestion(@Valid @RequestBody InTrainingQuestionRequest request) {
		log.info("createInTrainingQuestion :: request received for videoId={}", request.getVideoId());
		return inTrainingQuestionService.createInTrainingQuestion(request);
	}

	@GetMapping("/getInTrainingQuestionById/{id}")
	public ResponseEntity getInTrainingQuestionById(@PathVariable("id") Long id) {
		log.info("getInTrainingQuestionById :: request received for id={}", id);
		return inTrainingQuestionService.getInTrainingQuestionById(id);
	}

	@GetMapping("/getAllInTrainingQuestion")
	public ResponseEntity getAllInTrainingQuestion(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "questionId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllInTrainingQuestion :: request received with page={}, size={}", page, size);
		return inTrainingQuestionService.getAllInTrainingQuestion(page, size, sortBy, sortDir);
	}

	@GetMapping("/getQuestionsByVideoId/{videoId}")
	public ResponseEntity getQuestionsByVideoId(@PathVariable("videoId") Long videoId) {
		log.info("getQuestionsByVideoId :: request received for videoId={}", videoId);
		return inTrainingQuestionService.getQuestionsByVideoId(videoId);
	}

	@DeleteMapping("/deleteInTrainingQuestion/{id}")
	public ResponseEntity deleteInTrainingQuestion(@PathVariable("id") Long id) {
		log.info("deleteInTrainingQuestion :: request received for id={}", id);
		return inTrainingQuestionService.deleteInTrainingQuestion(id);
	}
}