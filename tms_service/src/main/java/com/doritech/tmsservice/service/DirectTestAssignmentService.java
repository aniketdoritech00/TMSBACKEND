package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.DirectTestAssignmentRequest;
import com.doritech.tmsservice.response.DirectTestAssignmentResponse;
import com.doritech.tmsservice.tms.entity.DirectTestAssignment;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface DirectTestAssignmentService {

	ResponseEntity createDirectTestAssignment(DirectTestAssignmentRequest request);

	ResponseEntity getMyDirectTestSets();

	ResponseEntity getDirectTestSetsById(Long id);

	ResponseEntity deleteById(Long id);

	ResponseEntity updateDirectTestAssignment(Long id, DirectTestAssignmentRequest request);

	ResponseEntity getAllDirectTestSet(int page, int size);

	DirectTestAssignmentResponse convertToResponse(DirectTestAssignment assignment);
}
