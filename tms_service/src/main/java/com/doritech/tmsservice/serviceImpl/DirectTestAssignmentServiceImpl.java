
package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

		// 1. Validate request
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

		// 2. Find an active, published Direct Test
		TestSet testSet = testSetRepository
				.findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(testSetId).orElse(null);

		if (testSet == null) {
			return new ResponseEntity("Published active Direct Test not found", HttpStatus.NOT_FOUND.value(), null);
		}

		// 3. Convert requested user ID to the UserMaster ID type
		Integer targetUserId;

		try {
			targetUserId = Math.toIntExact(requestedUserId);
		} catch (ArithmeticException ex) {
			return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
		}

		// 4. Validate the user receiving the test
		UserMaster user = userRepository.findById(targetUserId).orElse(null);

		if (user == null) {
			return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
		}

		// 5. Prevent duplicate assignment
		boolean alreadyAssigned = assignmentRepository.existsByTestSet_TestSetIdAndUserId(testSetId, requestedUserId);

		if (alreadyAssigned) {
			return new ResponseEntity("Direct Test is already assigned to this user", HttpStatus.CONFLICT.value(),
					null);
		}

		// 6. Identify the currently logged-in assigning user
		Long currentUserIdLong = CurrentUser.getUserId();

		if (currentUserIdLong == null || currentUserIdLong <= 0) {
			return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
		}

		Integer currentUserId;

		try {
			currentUserId = Math.toIntExact(currentUserIdLong);
		} catch (ArithmeticException ex) {
			return new ResponseEntity("Invalid current user id", HttpStatus.UNAUTHORIZED.value(), null);
		}

		UserMaster assignedBy = userRepository.findById(currentUserId).orElse(null);

		if (assignedBy == null) {
			return new ResponseEntity("Assigning user not found", HttpStatus.NOT_FOUND.value(), null);
		}
		
		Long currentUserIdLong1 = CurrentUser.getUserId();

		System.out.println("Current logged-in user ID: " + currentUserIdLong1);

		// 7. Create and save the Direct Test assignment
		try {
			DirectTestAssignment assignment = new DirectTestAssignment();

			assignment.setTestSet(testSet);
			assignment.setUserId(user.getUserId().longValue());
			assignment.setAssignedBy(assignedBy.getUserId().longValue());
			assignment.setAssignedAt(LocalDateTime.now());
			assignment.setDueDate(request.getDueDate());

			assignment.setStatus(AssignmentStatus.NOT_STARTED);
			assignment.setProgressPercentage(new BigDecimal("0.00"));
			assignment.setIsPassed(false);

			// Uncomment these lines after adding the field and
			// getter/setter to DirectTestAssignment, if required.
			// assignment.setCertificateGenerated(false);

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

		// Uncomment after adding certificateGenerated to the entity.
		// response.setCertificateGenerated(
		// assignment.isCertificateGenerated());

		response.setAttemptedQuestions(assignment.getAttemptedQuestions());
		response.setCorrectAnswers(assignment.getCorrectAnswers());
		response.setWrongAnswers(assignment.getWrongAnswers());
		response.setTotalQuestions(assignment.getTotalQuestions());
		response.setRemarks(assignment.getRemarks());
		response.setStartedAt(assignment.getStartedAt());

		return response;
	}
}
