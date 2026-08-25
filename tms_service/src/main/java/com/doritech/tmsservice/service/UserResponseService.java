package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.UserResponseRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface UserResponseService {

	ResponseEntity submitResponse(UserResponseRequest request);

	ResponseEntity getResponseById(Long userResponseId);

	ResponseEntity getResponsesByTestAttemptId(Long testAttemptId);

	ResponseEntity getResponseByAttemptAndQuestion(Long testAttemptId, Long testQuestionId);

	ResponseEntity updateResponse(Long userResponseId, UserResponseRequest request);

	ResponseEntity deleteResponse(Long userResponseId);

	ResponseEntity deleteResponsesByTestAttemptId(Long testAttemptId);

	ResponseEntity getCorrectResponses(Long testAttemptId);

	ResponseEntity getResponseCount(Long testAttemptId);

	ResponseEntity getCorrectAnswerCount(Long testAttemptId);
}