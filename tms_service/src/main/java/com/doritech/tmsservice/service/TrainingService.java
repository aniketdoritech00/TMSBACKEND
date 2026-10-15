package com.doritech.tmsservice.service;

import com.doritech.tmsservice.enums.TrainingStatus;
import com.doritech.tmsservice.request.TrainingRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingService {

    ResponseEntity createTraining(TrainingRequest trainingRequest);

    ResponseEntity getTrainingById(Long id);

    ResponseEntity getAllTraining(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteTraining(Long id);

    ResponseEntity publishTraining(Long id);

	ResponseEntity getAllTraining();

	ResponseEntity updateTraining(Long id, TrainingRequest request);

	ResponseEntity getTrainingByCategoryId(Long trainingCategoryId);

	ResponseEntity getAllTrainingFilter(int page, int size, String sortBy, String sortDir, Long trainingCategoryId,
			String trainingName, TrainingStatus status);
}