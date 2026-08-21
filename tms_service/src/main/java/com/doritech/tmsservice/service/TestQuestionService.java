package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TestQuestionRequest;

public interface TestQuestionService {

    ResponseEntity createTestQuestion(TestQuestionRequest request);

    ResponseEntity getTestQuestionById(Long id);

    ResponseEntity getAllTestQuestion(int page, int size, String sortBy, String sortDir);

    ResponseEntity getQuestionsByTestSetId(Long testSetId);

    ResponseEntity deleteTestQuestion(Long id);
}