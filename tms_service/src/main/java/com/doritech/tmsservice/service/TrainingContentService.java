package com.doritech.tmsservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingContentRequest;

public interface TrainingContentService {

    ResponseEntity createTrainingContent(TrainingContentRequest request, MultipartFile file);

    ResponseEntity getTrainingContentById(Long id);

    ResponseEntity getAllTrainingContent(int page, int size, String sortBy, String sortDir);

    ResponseEntity getContentByTrainingId(Long trainingId);

    ResponseEntity deleteTrainingContent(Long id);
}