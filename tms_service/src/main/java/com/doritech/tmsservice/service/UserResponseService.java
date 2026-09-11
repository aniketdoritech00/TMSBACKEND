package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.UserResponseRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface UserResponseService {

	ResponseEntity submitResponse(UserResponseRequest request);

	ResponseEntity getResponseById(Long userResponseId);

	ResponseEntity getAllResponses(int page, int size, String sortBy, String sortDir);

	ResponseEntity getResponsesByTestAttemptId(Long testAttemptId, int page, int size, String sortBy, String sortDir);

	ResponseEntity getResponseByAttemptAndQuestion(Long testAttemptId, Long testQuestionId);

	ResponseEntity updateResponse(Long userResponseId, UserResponseRequest request);

	ResponseEntity deleteResponse(Long userResponseId);

	ResponseEntity deleteResponsesByTestAttemptId(Long testAttemptId);

	ResponseEntity getCorrectResponses(Long testAttemptId, int page, int size, String sortBy, String sortDir);

	ResponseEntity getResponseCount(Long testAttemptId);

	ResponseEntity getCorrectAnswerCount(Long testAttemptId);
}