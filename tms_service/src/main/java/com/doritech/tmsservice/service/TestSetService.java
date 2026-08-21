package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TestSetRequest;

public interface TestSetService {

    ResponseEntity createTestSet(TestSetRequest request);

    ResponseEntity getTestSetById(Long id);

    ResponseEntity getAllTestSet(int page, int size, String sortBy, String sortDir);

    ResponseEntity getTestSetsByTrainingId(Long trainingId);

    ResponseEntity deleteTestSet(Long id);

    ResponseEntity publishTestSet(Long id);
}