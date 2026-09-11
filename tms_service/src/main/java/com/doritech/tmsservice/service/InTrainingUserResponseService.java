package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.InTrainingUserResponseRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface InTrainingUserResponseService {

	ResponseEntity submitResponse(InTrainingUserResponseRequest request);

	ResponseEntity getResponseById(Long inTrainingUserResponseId);

	ResponseEntity getAllResponses(int page, int size, String sortBy, String sortDir);

	ResponseEntity getResponsesByTrainingAssignment(Long trainingAssignmentId, int page, int size, String sortBy,
			String sortDir);

	ResponseEntity getResponsesByQuestion(Long questionId, int page, int size, String sortBy, String sortDir);

	ResponseEntity getResponseByQuestionAndAssignment(Long questionId, Long trainingAssignmentId);

	ResponseEntity updateResponse(Long inTrainingUserResponseId, InTrainingUserResponseRequest request);

	ResponseEntity deleteResponse(Long inTrainingUserResponseId);

	ResponseEntity deleteResponsesByTrainingAssignment(Long trainingAssignmentId);

	ResponseEntity getCorrectAnswerCount(Long trainingAssignmentId);

	ResponseEntity getSkippedAnswerCount(Long trainingAssignmentId);

	ResponseEntity getResponseCount(Long trainingAssignmentId);
}