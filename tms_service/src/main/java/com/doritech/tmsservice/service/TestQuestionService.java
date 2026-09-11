package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TestQuestionRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TestQuestionService {

	ResponseEntity createTestQuestion(TestQuestionRequest request);

	ResponseEntity getTestQuestionById(Long id);

	ResponseEntity getAllTestQuestions();

	ResponseEntity getAllTestQuestions(int page, int size, String sortBy, String sortDir);

	ResponseEntity getTestQuestionsByTestSetId(Long testSetId);

	ResponseEntity getTestQuestionsByTestSetId(Long testSetId, int page, int size, String sortBy, String sortDir);

	ResponseEntity updateTestQuestion(Long id, TestQuestionRequest request);

	ResponseEntity deleteTestQuestion(Long id);
}