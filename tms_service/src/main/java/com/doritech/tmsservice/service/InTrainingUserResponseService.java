package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.InTrainingUserResponseRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface InTrainingUserResponseService {

	ResponseEntity submitResponse(InTrainingUserResponseRequest request);

	ResponseEntity getResponseById(Long inTrainingUserResponseId);

	ResponseEntity getResponsesByTrainingAssignment(Long trainingAssignmentId);

	ResponseEntity getResponsesByQuestion(Long questionId);

	ResponseEntity getResponseByQuestionAndAssignment(Long questionId, Long trainingAssignmentId);

	ResponseEntity updateResponse(Long inTrainingUserResponseId, InTrainingUserResponseRequest request);

	ResponseEntity deleteResponse(Long inTrainingUserResponseId);

	ResponseEntity deleteResponsesByTrainingAssignment(Long trainingAssignmentId);

	ResponseEntity getCorrectAnswerCount(Long trainingAssignmentId);

	ResponseEntity getSkippedAnswerCount(Long trainingAssignmentId);

	ResponseEntity getResponseCount(Long trainingAssignmentId);
}