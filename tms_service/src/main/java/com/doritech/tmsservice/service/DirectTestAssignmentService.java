package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.DirectTestAssignmentRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface DirectTestAssignmentService {

	ResponseEntity createDirectTestAssignment(DirectTestAssignmentRequest request);
}
