
package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createDirectTestAssignment(DirectTestAssignmentRequest request) {

		Long currentUserId = CurrentUser.getUserId();

		if (request == null) {
			return new ResponseEntity("Direct Test assignment data is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		Long testSetId = request.getTestSetId();
		Long requestedUserId = request.getUserId();

		if (testSetId == null || testSetId <= 0) {
			return new ResponseEntity("Valid test set id is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (requestedUserId == null || requestedUserId <= 0) {
			return new ResponseEntity("Valid user id is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		TestSet testSet = testSetRepository
				.findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(testSetId).orElse(null);

		if (testSet == null) {
			return new ResponseEntity("Published active Direct Test not found", HttpStatus.NOT_FOUND.value(), null);
		}

		// 5. Prevent duplicate assignment
		boolean alreadyAssigned = assignmentRepository.existsByTestSet_TestSetIdAndUserId(testSetId, requestedUserId);

		if (alreadyAssigned) {
			return new ResponseEntity("Direct Test is already assigned to this user", HttpStatus.CONFLICT.value(),
					null);
		}

		if (currentUserId == null || currentUserId <= 0) {
			return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
		}

		UserMaster assignedBy = userRepository.findById(currentUserId.intValue()).orElse(null);

		if (assignedBy == null) {
			return new ResponseEntity("Assigning user not found", HttpStatus.NOT_FOUND.value(), null);
		}
		try {
			DirectTestAssignment assignment = new DirectTestAssignment();

			assignment.setTestSet(testSet);
			assignment.setUserId(request.getUserId());
			assignment.setAssignedBy(assignedBy.getUserId().longValue());
			assignment.setAssignedAt(LocalDateTime.now());
			assignment.setDueDate(request.getDueDate());

			assignment.setStatus(AssignmentStatus.NOT_STARTED);
			assignment.setProgressPercentage(new BigDecimal("0.00"));
			assignment.setIsPassed(false);

			assignment.setAttemptedQuestions(0);
			assignment.setCorrectAnswers(0);
			assignment.setWrongAnswers(0);
			assignment.setTotalQuestions(0);
			assignment.setStartedAt(null);

			DirectTestAssignment saved = assignmentRepository.saveAndFlush(assignment);

			DirectTestAssignmentResponse payload = convertToResponse(saved);

			return new ResponseEntity("Direct Test assigned successfully", HttpStatus.CREATED.value(), payload);

		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity("Direct Test assignment conflicts with an existing database record",
					HttpStatus.CONFLICT.value(), null);
		}
	}

	// 8. Convert the saved entity into the API response DTO
	// =========================

	private DirectTestAssignmentResponse convertToResponse(DirectTestAssignment assignment) {

		DirectTestAssignmentResponse response = new DirectTestAssignmentResponse();

		response.setDirectTestAssignmentId(assignment.getDirectTestAssignmentId());

		response.setTestSetId(assignment.getTestSet().getTestSetId());

		response.setTestName(assignment.getTestSet().getTestName());

		response.setUserId(assignment.getUserId());
		response.setAssignedBy(assignment.getAssignedBy());

		if (assignment.getAssignedBy() != null) {
			int id = Math.toIntExact(assignment.getAssignedBy());

			if (userRepository.findById(id).isPresent()) {
				response.setAssignedByName(userRepository.findEmployeeNameByUserId(id));
			}
		}

		if (assignment.getUserId() != null) {
			int id = Math.toIntExact(assignment.getUserId());

			if (userRepository.findById(id).isPresent()) {
				response.setUserName(userRepository.findEmployeeNameByUserId(id));
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

	 // ================= Direct Test Assignment==============
	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getMyDirectTestSets() {

		Long currentUserId = CurrentUser.getUserId();

		try {
			// Validate the logged-in user
			if (currentUserId == null || currentUserId <= 0) {
				return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
			}

			// Fetch direct test assignments for the current user
			List<DirectTestAssignment> assignments = assignmentRepository.findByUserId(currentUserId);

			// Convert entities into response DTOs
			List<DirectTestAssignmentResponse> payload = assignments.stream().map(this::convertToResponse)
					.collect(java.util.stream.Collectors.toList());

			// Return successful response
			return new ResponseEntity("Direct test sets fetched successfully", HttpStatus.OK.value(), payload);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Failed to fetch direct test sets", HttpStatus.INTERNAL_SERVER_ERROR.value(),
					null);
		}
	}

}
