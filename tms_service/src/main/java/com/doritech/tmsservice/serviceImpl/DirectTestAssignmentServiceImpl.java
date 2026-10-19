package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
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
import com.doritech.tmsservice.tms.entity.Batch;
import com.doritech.tmsservice.tms.entity.DirectTestAssignment;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.entity.UserBatch;
import com.doritech.tmsservice.tms.repository.DirectTestAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TestSetRepository;
import com.doritech.tmsservice.tms.repository.UserBatchRepository;

@Service
public class DirectTestAssignmentServiceImpl implements DirectTestAssignmentService {

	private final DirectTestAssignmentRepository assignmentRepository;
	private final TestSetRepository testSetRepository;
	private final UserMasterRepository userRepository;
	private final UserBatchRepository userBatchRepository;

	public DirectTestAssignmentServiceImpl(DirectTestAssignmentRepository assignmentRepository,
			TestSetRepository testSetRepository, UserMasterRepository userRepository,
			UserBatchRepository userBatchRepository) {

		this.assignmentRepository = assignmentRepository;
		this.testSetRepository = testSetRepository;
		this.userRepository = userRepository;
		this.userBatchRepository = userBatchRepository;
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

		// 1. Validate request
		if (request == null) {
			return errorResponse("Direct Test assignment data is required", HttpStatus.BAD_REQUEST);
		}

		Long testSetId = request.getTestSetId();
		Long requestedUserId = request.getUserId();
		Long batchId = request.getBatchId();

		if (testSetId == null || testSetId <= 0) {
			return errorResponse("Valid test set id is required", HttpStatus.BAD_REQUEST);
		}

		// At least one target is required.
		// Both userId and batchId may be provided together.
		if (requestedUserId == null && batchId == null) {
			return errorResponse("Provide either userId or batchId", HttpStatus.BAD_REQUEST);
		}

		if (requestedUserId != null && requestedUserId <= 0) {
			return errorResponse("Valid user id is required", HttpStatus.BAD_REQUEST);
		}

		if (batchId != null && batchId <= 0) {
			return errorResponse("Valid batch id is required", HttpStatus.BAD_REQUEST);
		}

		// 2. Find active, published Direct Test
		TestSet testSet = testSetRepository
				.findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(testSetId).orElse(null);

		if (testSet == null) {
			return errorResponse("Published active Direct Test not found", HttpStatus.NOT_FOUND);
		}

		// 3. Find the logged-in assigning user
		Long currentUserId = CurrentUser.getUserId();

		if (currentUserId == null || currentUserId <= 0) {
			return errorResponse("Unable to identify current user", HttpStatus.UNAUTHORIZED);
		}

		Optional<UserMaster> currentUser = userRepository.findById(currentUserId.intValue());
		UserMaster assignedBy = currentUser.get();

		if (assignedBy == null) {
			return errorResponse("Assigning user not found", HttpStatus.NOT_FOUND);
		}

		try {
			List<DirectTestAssignment> assignmentsToSave = new java.util.ArrayList<>();

			List<Long> targetUserIds = new java.util.ArrayList<>();

			Batch batch = null;

			// 4. Collect employees from the batch, if supplied
			if (batchId != null) {

				List<UserBatch> batchMembers = userBatchRepository.findAllByBatch_BatchId(batchId);

				if (batchMembers == null || batchMembers.isEmpty()) {
					return errorResponse("No employees found for the requested batch", HttpStatus.NOT_FOUND);
				}

				batch = batchMembers.get(0).getBatch();

				if (batch == null) {
					return errorResponse("Batch details not found", HttpStatus.NOT_FOUND);
				}

				targetUserIds.addAll(batchMembers.stream().map(UserBatch::getUserId).filter(java.util.Objects::nonNull)
						.distinct().collect(java.util.stream.Collectors.toList()));
			}

			// 5. Add the individually requested employee, if supplied
			if (requestedUserId != null) {
				if (!targetUserIds.contains(requestedUserId)) {
					targetUserIds.add(requestedUserId);
				}
			}

			if (targetUserIds.isEmpty()) {
				return errorResponse("No valid employees found for this assignment", HttpStatus.NOT_FOUND);
			}

			// 6. Validate that an individually requested user exists
			if (requestedUserId != null) {

				Integer requestedUserIdInt;

				try {
					requestedUserIdInt = Math.toIntExact(requestedUserId);
				} catch (ArithmeticException ex) {
					return errorResponse("Invalid user id", HttpStatus.BAD_REQUEST);
				}

				boolean userExists = userRepository.existsById(requestedUserIdInt);

				if (!userExists) {
					return errorResponse("User not found", HttpStatus.NOT_FOUND);
				}
			}

			// 7. Create assignments, skipping existing ones
			int skippedCount = 0;

			for (Long userId : targetUserIds) {

				boolean alreadyAssigned = assignmentRepository.existsByTestSet_TestSetIdAndUserId(testSetId, userId);

				if (alreadyAssigned) {
					skippedCount++;
					continue;
				}

				DirectTestAssignment assignment = buildAssignment(testSet, userId, assignedBy);

				assignment.setDueDate(request.getDueDate());

				// Preserve the batch reference for batch assignments.
				// If both targets are supplied, the individual employee
				// is included in the batch when already a member.
				if (batch != null) {
					assignment.setBatch(batch);
				} else {
					assignment.setBatch(null);
				}

				assignmentsToSave.add(assignment);
			}

			// 8. Return conflict if every target already has this test
			if (assignmentsToSave.isEmpty()) {
				return errorResponse("All selected employees already have this test assigned", HttpStatus.CONFLICT);
			}

			// 9. Save assignments
			List<DirectTestAssignment> savedAssignments = assignmentRepository.saveAllAndFlush(assignmentsToSave);

			// 10. Convert saved assignments to response objects
			List<DirectTestAssignmentResponse> payload = savedAssignments.stream().map(this::convertToResponse)
					.collect(java.util.stream.Collectors.toList());

			String message = "Direct Test assignment completed successfully. " + savedAssignments.size()
					+ ", skipped existing assignments: " + skippedCount;

			return new ResponseEntity(message, HttpStatus.CREATED.value(), payload);

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
		
		System.out.println("=======================Current USer id " + currentUserId);

		try {
			if (currentUserId == null || currentUserId <= 0) {
				return errorResponse("Unable to identify current user", HttpStatus.UNAUTHORIZED);
			}

			// 1. Get test sets assigned directly to the current user
			List<DirectTestAssignment> assignments = new ArrayList<>(assignmentRepository.findByUserId(currentUserId));

			// 2. Get batches associated with the current user
			List<UserBatch> userBatches = userBatchRepository.findAllByUserId(currentUserId);

			List<Long> batchIds = userBatches.stream().map(userBatch -> userBatch.getBatch().getBatchId()).distinct()
					.collect(Collectors.toList());

			// 3. Get test sets assigned to those batches
			if (!batchIds.isEmpty()) {
				assignments.addAll(assignmentRepository.findByBatch_BatchIdIn(batchIds));
			}

			// 4. Remove duplicate assignments
			List<DirectTestAssignmentResponse> payload = assignments.stream()
					.collect(Collectors.toMap(DirectTestAssignment::getDirectTestAssignmentId, assignment -> assignment,
							(existing, duplicate) -> existing, LinkedHashMap::new))
					.values().stream().map(this::convertToResponse).collect(Collectors.toList());

			return new ResponseEntity("Direct test Assignment fetched successfully", HttpStatus.OK.value(), payload);

		} catch (Exception e) {
			e.printStackTrace();
			return errorResponse("Failed to fetch direct test sets", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
// This API is working fine but in this api you need to map an condition that is if i have assigned a test set to a batch and current user is 
//	mapped with that batch then show that test set to current user also and also to check that is user is associated with that batch or not you need to 
//  check from user batch

	// ================= COMMON ERROR RESPONSE =================

	private ResponseEntity errorResponse(String message, HttpStatus status) {

		return new ResponseEntity(message, status.value(), null);
	}
}