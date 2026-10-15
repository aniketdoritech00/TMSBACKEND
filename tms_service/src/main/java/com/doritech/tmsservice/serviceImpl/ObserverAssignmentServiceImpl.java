package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.request.ObserverAssignmentRequest;
import com.doritech.tmsservice.response.ObserverAssignmentResponse;
import com.doritech.tmsservice.service.ObserverAssignmentService;
import com.doritech.tmsservice.tms.entity.ObserverAssignment;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.repository.ObserverAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;

@Service
public class ObserverAssignmentServiceImpl implements ObserverAssignmentService {

	private final ObserverAssignmentRepository observerAssignmentRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;

	public ObserverAssignmentServiceImpl(ObserverAssignmentRepository observerAssignmentRepository,
			TrainingAssignmentRepository trainingAssignmentRepository) {

		this.observerAssignmentRepository = observerAssignmentRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
	}

	@Override
	public ResponseEntity createObserverAssignment(ObserverAssignmentRequest request) {

		Long currentUserId = CurrentUser.getUserId();

		ResponseEntity response = new ResponseEntity();

		if (request == null) {
			response.setMessage("Request is null");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setPayload(null);
			return response;
		}

		try {

			if (request.getTrainingAssignmentId() == null) {
				response.setMessage("Training Assignment ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			if (request.getUserId() == null) {
				response.setMessage("User ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			Optional<TrainingAssignment> trainingAssignmentOptional = trainingAssignmentRepository
					.findById(request.getTrainingAssignmentId());

			if (trainingAssignmentOptional.isEmpty()) {
				response.setMessage("Training Assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			boolean alreadyExists = observerAssignmentRepository
					.existsByUserIdAndTrainingAssignment_TrainingAssignmentId(request.getUserId(),
							request.getTrainingAssignmentId());

			if (alreadyExists) {
				response.setMessage("Observer is already assigned to this training assignment!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);
				return response;
			}

			ObserverAssignment observerAssignment = convertToEntity(request, trainingAssignmentOptional.get());

			observerAssignment.setCreatedBy(currentUserId);
			observerAssignment.setCreatedAt(LocalDateTime.now());

			ObserverAssignment savedObserverAssignment = observerAssignmentRepository.save(observerAssignment);

			ObserverAssignmentResponse observerAssignmentResponse = convertToResponse(savedObserverAssignment);

			response.setMessage("Observer assigned successfully!");
			response.setStatusCode(HttpStatus.CREATED.value());
			response.setPayload(observerAssignmentResponse);

		} catch (DataIntegrityViolationException e) {

			response.setMessage("Observer is already assigned to this training assignment!");
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setPayload(null);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity updateObserverAssignment(Long observerAssignmentId, ObserverAssignmentRequest request) {

		Long currentUserId = CurrentUser.getUserId();

		ResponseEntity response = new ResponseEntity();

		if (observerAssignmentId == null) {
			response.setMessage("Observer Assignment ID is required!");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setPayload(null);
			return response;
		}

		if (request == null) {
			response.setMessage("Request is null");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setPayload(null);
			return response;
		}

		try {

			Optional<ObserverAssignment> optionalObserverAssignment = observerAssignmentRepository
					.findById(observerAssignmentId);

			if (optionalObserverAssignment.isEmpty()) {
				response.setMessage("Observer Assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			if (request.getTrainingAssignmentId() == null) {
				response.setMessage("Training Assignment ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			if (request.getUserId() == null) {
				response.setMessage("User ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			Optional<TrainingAssignment> trainingAssignmentOptional = trainingAssignmentRepository
					.findById(request.getTrainingAssignmentId());

			if (trainingAssignmentOptional.isEmpty()) {
				response.setMessage("Training Assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			boolean alreadyExists = observerAssignmentRepository
					.existsByUserIdAndTrainingAssignment_TrainingAssignmentIdAndObserverAssignmentIdNot(
							request.getUserId(), request.getTrainingAssignmentId(), observerAssignmentId);

			if (alreadyExists) {
				response.setMessage("Observer is already assigned to this training assignment!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);
				return response;
			}

			ObserverAssignment observerAssignment = optionalObserverAssignment.get();

			observerAssignment.setUserId(request.getUserId());

			observerAssignment.setTrainingAssignment(trainingAssignmentOptional.get());

			/*
			 * Your entity currently does not have updatedAt/updatedBy. Therefore only
			 * existing fields are updated here.
			 */

			ObserverAssignment updatedObserverAssignment = observerAssignmentRepository.save(observerAssignment);

			ObserverAssignmentResponse observerAssignmentResponse = convertToResponse(updatedObserverAssignment);

			response.setMessage("Observer Assignment updated successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(observerAssignmentResponse);

		} catch (DataIntegrityViolationException e) {

			response.setMessage("Observer is already assigned to this training assignment!");
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setPayload(null);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Error while updating observer assignment");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity getAllObserverAssignment() {

		ResponseEntity response = new ResponseEntity();

		try {

			List<ObserverAssignment> observerAssignments = observerAssignmentRepository.findAll();

			if (observerAssignments.isEmpty()) {
				response.setMessage("No observer assignments found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			List<ObserverAssignmentResponse> observerAssignmentResponses = observerAssignments.stream()
					.map(this::convertToResponse).toList();

			response.setMessage("Observer assignments found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(observerAssignmentResponses);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity getObserverAssignmentById(Long observerAssignmentId) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (observerAssignmentId == null) {
				response.setMessage("Observer Assignment ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			Optional<ObserverAssignment> optionalObserverAssignment = observerAssignmentRepository
					.findById(observerAssignmentId);

			if (optionalObserverAssignment.isEmpty()) {
				response.setMessage("Observer Assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			ObserverAssignmentResponse observerAssignmentResponse = convertToResponse(optionalObserverAssignment.get());

			response.setMessage("Observer Assignment found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(observerAssignmentResponse);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity deleteObserverAssignmentById(Long observerAssignmentId) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (observerAssignmentId == null) {
				response.setMessage("Observer Assignment ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			Optional<ObserverAssignment> optionalObserverAssignment = observerAssignmentRepository
					.findById(observerAssignmentId);

			if (optionalObserverAssignment.isEmpty()) {
				response.setMessage("Observer Assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			observerAssignmentRepository.delete(optionalObserverAssignment.get());

			response.setMessage("Observer Assignment deleted successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(null);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	private ObserverAssignment convertToEntity(ObserverAssignmentRequest request,
			TrainingAssignment trainingAssignment) {

		ObserverAssignment observerAssignment = new ObserverAssignment();

		observerAssignment.setUserId(request.getUserId());
		observerAssignment.setTrainingAssignment(trainingAssignment);
		observerAssignment.setCreatedAt(LocalDateTime.now());

		return observerAssignment;
	}

	private ObserverAssignmentResponse convertToResponse(ObserverAssignment observerAssignment) {

		ObserverAssignmentResponse response = new ObserverAssignmentResponse();

		response.setObserverAssignmentId(observerAssignment.getObserverAssignmentId());

		response.setUserId(observerAssignment.getUserId());

		if (observerAssignment.getTrainingAssignment() != null) {
			response.setTrainingAssignmentId(observerAssignment.getTrainingAssignment().getTrainingAssignmentId());
		}

		response.setCreatedAt(observerAssignment.getCreatedAt());

		response.setCreatedBy(observerAssignment.getCreatedBy());

		return response;
	}
}
