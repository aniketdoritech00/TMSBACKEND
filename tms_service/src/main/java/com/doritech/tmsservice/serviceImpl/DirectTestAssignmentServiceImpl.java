package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.AssignmentStatus;
import com.doritech.tmsservice.erp.entity.UserMaster;
import com.doritech.tmsservice.erp.repository.UserMasterRepository;
import com.doritech.tmsservice.request.DirectTestAssignmentRequest;
import com.doritech.tmsservice.response.DirectTestAssignmentResponse;
import com.doritech.tmsservice.service.DirectTestAssignmentService;
import com.doritech.tmsservice.tms.entity.DirectTestAssignment;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.repository.DirectTestAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TestSetRepository;

@Service
public class DirectTestAssignmentServiceImpl implements DirectTestAssignmentService {

	private final DirectTestAssignmentRepository assignmentRepository;
	private final TestSetRepository testSetRepository;
	private final UserMasterRepository userRepository;

	public DirectTestAssignmentServiceImpl(DirectTestAssignmentRepository assignmentRepository,
			TestSetRepository testSetRepository, UserMasterRepository userRepository) {

		this.assignmentRepository = assignmentRepository;
		this.testSetRepository = testSetRepository;
		this.userRepository = userRepository;
	}

	// ================= BUILD ASSIGNMENT =================

	private DirectTestAssignment buildAssignment(TestSet testSet, Long userId, UserMaster assignedBy) {

		DirectTestAssignment assignment = new DirectTestAssignment();

		assignment.setTestSet(testSet);
		assignment.setUserId(userId);
		assignment.setAssignedBy(assignedBy.getUserId().longValue());
		assignment.setAssignedAt(LocalDateTime.now());
		assignment.setDueDate(null); // Set below from the request in the calling method
		assignment.setStatus(AssignmentStatus.NOT_STARTED);
		assignment.setProgressPercentage(new BigDecimal("0.00"));
		assignment.setIsPassed(false);
		assignment.setAttemptedQuestions(0);
		assignment.setCorrectAnswers(0);
		assignment.setWrongAnswers(0);
		assignment.setTotalQuestions(0);
		assignment.setStartedAt(null);

		return assignment;
	}

	// ================= CREATE DIRECT TEST ASSIGNMENT =================

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createDirectTestAssignment(DirectTestAssignmentRequest request) {

		if (request == null) {
			return errorResponse("Direct Test assignment data is required", HttpStatus.BAD_REQUEST);
		}

		Long testSetId = request.getTestSetId();
		Long requestedUserId = request.getUserId();
		Long batchId = request.getBatchId();

		// Validate test set ID
		if (testSetId == null || testSetId <= 0) {
			return errorResponse("Valid test set id is required", HttpStatus.BAD_REQUEST);
		}

		// Validate that exactly one target is provided
		if (requestedUserId == null && batchId == null) {
			return errorResponse("Either userId or batchId is required", HttpStatus.BAD_REQUEST);
		}

		if (requestedUserId != null && batchId != null) {
			return errorResponse("Provide either userId or batchId, not both", HttpStatus.BAD_REQUEST);
		}

		// Validate individual employee ID
		if (requestedUserId != null && requestedUserId <= 0) {
			return errorResponse("Valid user id is required", HttpStatus.BAD_REQUEST);
		}

		// Validate batch ID
		if (batchId != null && batchId <= 0) {
			return errorResponse("Valid batch id is required", HttpStatus.BAD_REQUEST);
		}

		// Fetch an active, published Direct Test
		TestSet testSet = testSetRepository
				.findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(testSetId).orElse(null);

		if (testSet == null) {
			return errorResponse("Published active Direct Test not found", HttpStatus.NOT_FOUND);
		}

		// Identify the logged-in assigning user
		Long currentUserId = CurrentUser.getUserId();

		if (currentUserId == null || currentUserId <= 0) {
			return errorResponse("Unable to identify current user", HttpStatus.UNAUTHORIZED);
		}

		UserMaster assignedBy = userRepository.findById(currentUserId.intValue()).orElse(null);

		if (assignedBy == null) {
			return errorResponse("Assigning user not found", HttpStatus.NOT_FOUND);
		}

		// Batch assignment implementation is required here.
		if (batchId != null) {
			return errorResponse("Batch assignment is not implemented yet", HttpStatus.NOT_IMPLEMENTED);
		}

		// Prevent duplicate individual assignment
		boolean alreadyAssigned = assignmentRepository.existsByTestSet_TestSetIdAndUserId(testSetId, requestedUserId);

		if (alreadyAssigned) {
			return errorResponse("Direct Test is already assigned to this user", HttpStatus.CONFLICT);
		}

		try {
			DirectTestAssignment assignment = buildAssignment(testSet, requestedUserId, assignedBy);

			// Preserve the due date from the request
			assignment.setDueDate(request.getDueDate());

			DirectTestAssignment saved = assignmentRepository.saveAndFlush(assignment);

			DirectTestAssignmentResponse payload = convertToResponse(saved);

			return new ResponseEntity("Direct Test assigned successfully", HttpStatus.CREATED.value(), payload);

		} catch (DataIntegrityViolationException ex) {
			return errorResponse("Direct Test assignment conflicts with an existing database record",
					HttpStatus.CONFLICT);
		}
	}
	// ================= CONVERT ENTITY TO RESPONSE =================

	private DirectTestAssignmentResponse convertToResponse(DirectTestAssignment assignment) {

		DirectTestAssignmentResponse response = new DirectTestAssignmentResponse();

		response.setDirectTestAssignmentId(assignment.getDirectTestAssignmentId());

		response.setTestSetId(assignment.getTestSet().getTestSetId());

		response.setTestName(assignment.getTestSet().getTestName());

		response.setUserId(assignment.getUserId());
		response.setAssignedBy(assignment.getAssignedBy());

		if (assignment.getAssignedBy() != null) {
			int assignedById = Math.toIntExact(assignment.getAssignedBy());

			if (userRepository.findById(assignedById).isPresent()) {
				response.setAssignedByName(userRepository.findEmployeeNameByUserId(assignedById));
			}
		}

		if (assignment.getUserId() != null) {
			int userId = Math.toIntExact(assignment.getUserId());

			if (userRepository.findById(userId).isPresent()) {
				response.setUserName(userRepository.findEmployeeNameByUserId(userId));
			}
		}

		response.setAssignedAt(assignment.getAssignedAt());
		response.setDueDate(assignment.getDueDate());
		response.setStatus(assignment.getStatus());
		response.setProgressPercentage(assignment.getProgressPercentage());
		response.setCompletionDate(assignment.getCompletionDate());
		response.setFinalScore(assignment.getFinalScore());
		response.setIsPassed(assignment.getIsPassed());
		response.setAttemptedQuestions(assignment.getAttemptedQuestions());
		response.setCorrectAnswers(assignment.getCorrectAnswers());
		response.setWrongAnswers(assignment.getWrongAnswers());
		response.setTotalQuestions(assignment.getTotalQuestions());
		response.setRemarks(assignment.getRemarks());
		response.setStartedAt(assignment.getStartedAt());

		return response;
	}

	// ================= FETCH MY DIRECT TEST SETS =================

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getMyDirectTestSets() {

		Long currentUserId = CurrentUser.getUserId();

		try {
			if (currentUserId == null || currentUserId <= 0) {
				return errorResponse("Unable to identify current user", HttpStatus.UNAUTHORIZED);
			}

			List<DirectTestAssignment> assignments = assignmentRepository.findByUserId(currentUserId);

			List<DirectTestAssignmentResponse> payload = assignments.stream().map(this::convertToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Direct test sets fetched successfully", HttpStatus.OK.value(), payload);

		} catch (Exception e) {
			e.printStackTrace();

			return errorResponse("Failed to fetch direct test sets", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ================= COMMON ERROR RESPONSE =================

	private ResponseEntity errorResponse(String message, HttpStatus status) {

		return new ResponseEntity(message, status.value(), null);
	}
}