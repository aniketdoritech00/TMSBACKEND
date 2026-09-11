package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.TestSetRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface TestSetService {

	ResponseEntity createTestSet(TestSetRequest request);

	ResponseEntity getTestSetById(Long id);

	ResponseEntity getAllTestSets();

	ResponseEntity getAllTestSets(int page, int size, String sortBy, String sortDir);

	ResponseEntity getTestSetsByTrainingId(Long trainingId);

	ResponseEntity getTestSetsByTrainingId(Long trainingId, int page, int size, String sortBy, String sortDir);

	ResponseEntity updateTestSet(Long id, TestSetRequest request);

	ResponseEntity deleteTestSet(Long id);
}