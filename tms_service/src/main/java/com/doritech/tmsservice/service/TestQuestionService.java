package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TestQuestionRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TestQuestionService {

    ResponseEntity createTestQuestion(TestQuestionRequest request);

    ResponseEntity getTestQuestionById(Long id);

    ResponseEntity getAllTestQuestion(int page, int size, String sortBy, String sortDir);

    ResponseEntity getQuestionsByTestSetId(Long testSetId);

    ResponseEntity deleteTestQuestion(Long id);
}