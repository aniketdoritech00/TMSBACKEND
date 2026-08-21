package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingCategoryRequest;

public interface TrainingCategoryService {

    ResponseEntity createTrainingCategory(TrainingCategoryRequest trainingCategoryRequest);

    ResponseEntity getTrainingCategoryById(Long id);

    ResponseEntity getAllTrainingCategory(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteTrainingCategory(Long id);
}