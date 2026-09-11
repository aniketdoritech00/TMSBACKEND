package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.InTrainingQuestionRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface InTrainingQuestionService {

	ResponseEntity createInTrainingQuestion(InTrainingQuestionRequest request);

	ResponseEntity getInTrainingQuestionById(Long id);

	ResponseEntity getAllInTrainingQuestions();

	ResponseEntity getAllInTrainingQuestions(int page, int size, String sortBy, String sortDir);

	ResponseEntity getInTrainingQuestionsByVideoId(Long videoId);

	ResponseEntity getInTrainingQuestionsByVideoId(Long videoId, int page, int size, String sortBy, String sortDir);

	ResponseEntity updateInTrainingQuestion(Long id, InTrainingQuestionRequest request);

	ResponseEntity deleteInTrainingQuestion(Long id);
}