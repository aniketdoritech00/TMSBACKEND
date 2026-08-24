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
import com.doritech.tmsservice.request.TrainingContentRequest;
import com.doritech.tmsservice.service.TrainingContentService;

@RestController
@RequestMapping("/api/tms/training-content")
public class TrainingContentController {

	private static final Logger log = LoggerFactory.getLogger(TrainingContentController.class);

	private final TrainingContentService trainingContentService;

	public TrainingContentController(TrainingContentService trainingContentService) {
		this.trainingContentService = trainingContentService;
	}

	@PostMapping(value = "/createTrainingContent", consumes = "multipart/form-data")
	public ResponseEntity createTrainingContent(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam("trainingId") Long trainingId, @RequestParam("contentType") String contentType,
			@RequestParam(value = "contentReferenceId", required = false) Integer contentReferenceId,
			@RequestParam(value = "documentName", required = false) String documentName,
			@RequestParam(value = "documentDescription", required = false) String documentDescription,
			@RequestParam(value = "documentUrl", required = false) String documentUrl,
			@RequestParam(value = "displayOrder", required = false) Integer displayOrder,
			@RequestParam(value = "isRequired", required = false) Boolean isRequired) {

		log.info("createTrainingContent :: request received for trainingId={}, contentType={}", trainingId,
				contentType);

		TrainingContentRequest request = new TrainingContentRequest();
		request.setTrainingId(trainingId);
		request.setContentType(contentType);
		request.setContentReferenceId(contentReferenceId);
		request.setDocumentName(documentName);
		request.setDocumentDescription(documentDescription);
		request.setDocumentUrl(documentUrl);
		request.setDisplayOrder(displayOrder);
		request.setIsRequired(isRequired);

		return trainingContentService.createTrainingContent(request, file);
	}

	@GetMapping("/getTrainingContentById/{id}")
	public ResponseEntity getTrainingContentById(@PathVariable("id") Long id) {
		log.info("getTrainingContentById :: request received for id={}", id);
		return trainingContentService.getTrainingContentById(id);
	}

	@GetMapping("/getAllTrainingContent")
	public ResponseEntity getAllTrainingContent(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "trainingContentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllTrainingContent :: request received with page={}, size={}", page, size);
		return trainingContentService.getAllTrainingContent(page, size, sortBy, sortDir);
	}

	@GetMapping("/getContentByTrainingId/{trainingId}")
	public ResponseEntity getContentByTrainingId(@PathVariable("trainingId") Long trainingId) {
		log.info("getContentByTrainingId :: request received for trainingId={}", trainingId);
		return trainingContentService.getContentByTrainingId(trainingId);
	}

	@DeleteMapping("/deleteTrainingContent/{id}")
	public ResponseEntity deleteTrainingContent(@PathVariable("id") Long id) {
		log.info("deleteTrainingContent :: request received for id={}", id);
		return trainingContentService.deleteTrainingContent(id);
	}
}