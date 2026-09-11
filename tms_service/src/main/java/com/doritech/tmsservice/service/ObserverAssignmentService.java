package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.ObserverAssignmentRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface ObserverAssignmentService {

	ResponseEntity createObserverAssignment(ObserverAssignmentRequest request);

	ResponseEntity updateObserverAssignment(Long observerAssignmentId, ObserverAssignmentRequest request);

	ResponseEntity getAllObserverAssignment();

	ResponseEntity getObserverAssignmentById(Long observerAssignmentId);

	ResponseEntity deleteObserverAssignmentById(Long observerAssignmentId);
}
