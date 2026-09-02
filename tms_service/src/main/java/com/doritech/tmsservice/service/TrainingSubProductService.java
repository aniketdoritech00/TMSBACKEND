package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TrainingSubProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TrainingSubProductService {

	ResponseEntity assignSubProductToTraining(TrainingSubProductRequest request);

	ResponseEntity getSubProductsByTrainingId(Long trainingId);

	ResponseEntity getTrainingsBySubProductId(Long subProductId);

	ResponseEntity deleteTrainingSubProduct(Long trainingId, Long subProductId);
}