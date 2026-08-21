package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingRequest;

public interface TrainingService {

    ResponseEntity createTraining(TrainingRequest trainingRequest);

    ResponseEntity getTrainingById(Long id);

    ResponseEntity getAllTraining(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteTraining(Long id);

    ResponseEntity publishTraining(Long id);
}