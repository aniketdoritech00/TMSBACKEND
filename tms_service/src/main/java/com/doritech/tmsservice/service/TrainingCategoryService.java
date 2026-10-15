package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TrainingCategoryRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingCategoryService {

    ResponseEntity createTrainingCategory(TrainingCategoryRequest trainingCategoryRequest);

    ResponseEntity getTrainingCategoryById(Long id);

    ResponseEntity getAllTrainingCategory(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteTrainingCategory(Long id);

	ResponseEntity getAllTrainingCategory();

	ResponseEntity updateTrainingCategory(Long id, TrainingCategoryRequest request);

}