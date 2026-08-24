package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.InTrainingQuestionRequest;

public interface InTrainingQuestionService {

    ResponseEntity createInTrainingQuestion(InTrainingQuestionRequest request);

    ResponseEntity getInTrainingQuestionById(Long id);

    ResponseEntity getAllInTrainingQuestion(int page, int size, String sortBy, String sortDir);

    ResponseEntity getQuestionsByVideoId(Long videoId);

    ResponseEntity deleteInTrainingQuestion(Long id);
}