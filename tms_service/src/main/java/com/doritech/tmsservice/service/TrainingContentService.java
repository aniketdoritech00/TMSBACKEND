package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TrainingContentRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingContentService {

	ResponseEntity createTrainingContent(TrainingContentRequest request);

	ResponseEntity getTrainingContentById(Long id);

	ResponseEntity getAllTrainingContent();

	ResponseEntity getTrainingContentByTrainingId(Long trainingId);

	ResponseEntity updateTrainingContent(Long id, TrainingContentRequest request);

	ResponseEntity deleteTrainingContent(Long id);

	ResponseEntity getAllTrainingContent(int page, int size, String sortBy, String sortDir);
}