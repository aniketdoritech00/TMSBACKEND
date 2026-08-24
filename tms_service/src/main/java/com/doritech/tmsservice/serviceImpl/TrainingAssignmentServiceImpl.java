package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.TrainingAssignment;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.request.TrainingAssignmentRequest;
import com.doritech.tmsservice.response.TrainingAssignmentListResponse;
import com.doritech.tmsservice.response.TrainingAssignmentResponse;
import com.doritech.tmsservice.service.TrainingAssignmentService;

@Service
public class TrainingAssignmentServiceImpl implements TrainingAssignmentService {

	private static final Logger log = LoggerFactory.getLogger(TrainingAssignmentServiceImpl.class);

	private final TrainingAssignmentRepository trainingAssignmentRepository;

	public TrainingAssignmentServiceImpl(TrainingAssignmentRepository trainingAssignmentRepository) {
		this.trainingAssignmentRepository = trainingAssignmentRepository;
	}

	@Override
	public ResponseEntity createTrainingAssignment(TrainingAssignmentRequest request) {

		log.info("createTrainingAssignment :: request received for trainingId={}, userId={}", request.getTrainingId(),
				request.getUserId());

		if (trainingAssignmentRepository.existsByTrainingIdAndUserId(request.getTrainingId(), request.getUserId())) {
			log.error("createTrainingAssignment :: duplicate assignment for trainingId={}, userId={}",
					request.getTrainingId(), request.getUserId());
			throw new ResourceAlreadyExistsException("This training is already assigned to the user");
		}

		Long currentUserId = CurrentUser.getUserId();

		TrainingAssignment assignment = new TrainingAssignment();
		assignment.setTrainingId(request.getTrainingId());
		assignment.setUserId(request.getUserId());
		assignment.setBatchId(request.getBatchId());
		assignment.setAssignedBy(currentUserId);
		assignment.setDueDate(request.getDueDate());

		TrainingAssignment saved;
		try {
			saved = trainingAssignmentRepository.save(assignment);
		} catch (Exception e) {
			log.error("createTrainingAssignment :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving training assignment");
		}

		log.info("createTrainingAssignment :: saved successfully with id={} by userId={}",
				saved.getTrainingAssignmentId(), currentUserId);

		return new ResponseEntity("Training assignment saved successfully", HttpStatus.CREATED.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getTrainingAssignmentById(Long id) {

		log.info("getTrainingAssignmentById :: request received for id={}", id);

		if (id == null) {
			log.error("getTrainingAssignmentById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingAssignment assignment = trainingAssignmentRepository.findById(id).orElseThrow(() -> {
			log.error("getTrainingAssignmentById :: not found for id={}", id);
			return new ResourceNotFoundException("Training assignment not found with id: " + id);
		});

		log.info("getTrainingAssignmentById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(assignment));
	}

	@Override
	public ResponseEntity getAllTrainingAssignment(int page, int size, String sortBy, String sortDir) {

		log.info("getAllTrainingAssignment :: request received with page={}, size={}, sortBy={}, sortDir={}", page,
				size, sortBy, sortDir);

		if (page < 0) {
			log.error("getAllTrainingAssignment :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllTrainingAssignment :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllTrainingAssignment :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<TrainingAssignment> assignmentPage;
		try {
			assignmentPage = trainingAssignmentRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllTrainingAssignment :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllTrainingAssignment :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching training assignments");
		}

		List<TrainingAssignmentListResponse> responseList = assignmentPage.getContent().stream()
				.map(this::mapToListResponse).collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", assignmentPage.getNumber());
		pageData.put("pageSize", assignmentPage.getSize());
		pageData.put("totalElements", assignmentPage.getTotalElements());
		pageData.put("totalPages", assignmentPage.getTotalPages());
		pageData.put("isLast", assignmentPage.isLast());

		log.info("getAllTrainingAssignment :: {} of {} assignments fetched successfully", responseList.size(),
				assignmentPage.getTotalElements());

		return new ResponseEntity("Training assignment fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getAssignmentsByUserId(Long userId) {

		log.info("getAssignmentsByUserId :: request received for userId={}", userId);

		if (userId == null) {
			log.error("getAssignmentsByUserId :: userId is null");
			throw new BadRequestException("User ID can not be null");
		}

		List<TrainingAssignment> assignmentList = trainingAssignmentRepository.findByUserId(userId);

		List<TrainingAssignmentListResponse> responseList = assignmentList.stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		log.info("getAssignmentsByUserId :: {} assignments fetched for userId={}", responseList.size(), userId);

		return new ResponseEntity("Training assignment fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity getAssignmentsByTrainingId(Long trainingId) {

		log.info("getAssignmentsByTrainingId :: request received for trainingId={}", trainingId);

		if (trainingId == null) {
			log.error("getAssignmentsByTrainingId :: trainingId is null");
			throw new BadRequestException("Training ID can not be null");
		}

		List<TrainingAssignment> assignmentList = trainingAssignmentRepository.findByTrainingId(trainingId);

		List<TrainingAssignmentListResponse> responseList = assignmentList.stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		log.info("getAssignmentsByTrainingId :: {} assignments fetched for trainingId={}", responseList.size(),
				trainingId);

		return new ResponseEntity("Training assignment fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity startTrainingAssignment(Long id) {

		log.info("startTrainingAssignment :: request received for id={}", id);

		if (id == null) {
			log.error("startTrainingAssignment :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingAssignment assignment = trainingAssignmentRepository.findById(id).orElseThrow(() -> {
			log.error("startTrainingAssignment :: not found for id={}", id);
			return new ResourceNotFoundException("Training assignment not found with id: " + id);
		});

		if (assignment.getStatus() == TrainingAssignment.Status.NOT_STARTED) {
			assignment.setStatus(TrainingAssignment.Status.IN_PROGRESS);
			assignment.setStartedAt(LocalDateTime.now());
		}

		TrainingAssignment saved;
		try {
			saved = trainingAssignmentRepository.save(assignment);
		} catch (Exception e) {
			log.error("startTrainingAssignment :: error while updating - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while starting training assignment");
		}

		log.info("startTrainingAssignment :: started successfully for id={}", id);

		return new ResponseEntity("Training assignment started successfully", HttpStatus.OK.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity deleteTrainingAssignment(Long id) {

		log.info("deleteTrainingAssignment :: request received for id={}", id);

		if (id == null) {
			log.error("deleteTrainingAssignment :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingAssignment assignment = trainingAssignmentRepository.findById(id).orElseThrow(() -> {
			log.error("deleteTrainingAssignment :: not found for id={}", id);
			return new ResourceNotFoundException("Training assignment not found with id: " + id);
		});

		try {
			trainingAssignmentRepository.delete(assignment);
		} catch (Exception e) {
			log.error("deleteTrainingAssignment :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException(
					"Cannot delete training assignment, it may be linked to other records");
		}

		log.info("deleteTrainingAssignment :: deleted successfully for id={}", id);

		return new ResponseEntity("Training assignment deleted successfully", HttpStatus.OK.value(), null);
	}

	private TrainingAssignmentResponse mapToFullResponse(TrainingAssignment entity) {
		TrainingAssignmentResponse response = new TrainingAssignmentResponse();
		response.setTrainingAssignmentId(entity.getTrainingAssignmentId());
		response.setTrainingId(entity.getTrainingId());
		response.setUserId(entity.getUserId());
		response.setBatchId(entity.getBatchId());
		response.setAssignedBy(entity.getAssignedBy());
		response.setAssignedAt(entity.getAssignedAt());
		response.setDueDate(entity.getDueDate());
		response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
		response.setProgressPercentage(entity.getProgressPercentage());
		response.setCompletionDate(entity.getCompletionDate());
		response.setFinalScore(entity.getFinalScore());
		response.setIsPassed(entity.getIsPassed());
		response.setCertificateGenerated(entity.getCertificateGenerated());
		response.setAttemptedQuestions(entity.getAttemptedQuestions());
		response.setCorrectAnswers(entity.getCorrectAnswers());
		response.setWrongAnswers(entity.getWrongAnswers());
		response.setTotalQuestions(entity.getTotalQuestions());
		response.setRemarks(entity.getRemarks());
		response.setStartedAt(entity.getStartedAt());
		return response;
	}

	private TrainingAssignmentListResponse mapToListResponse(TrainingAssignment entity) {
		return new TrainingAssignmentListResponse(entity.getTrainingAssignmentId(), entity.getTrainingId(),
				entity.getUserId(), entity.getStatus() != null ? entity.getStatus().name() : null,
				entity.getProgressPercentage());
	}
}