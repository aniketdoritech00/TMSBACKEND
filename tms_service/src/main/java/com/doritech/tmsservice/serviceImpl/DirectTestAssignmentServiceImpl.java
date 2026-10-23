package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.AssignmentStatus;
import com.doritech.tmsservice.erp.entity.UserMaster;
import com.doritech.tmsservice.erp.repository.UserMasterRepository;
import com.doritech.tmsservice.request.DirectTestAssignmentRequest;
import com.doritech.tmsservice.response.DirectTestAssignmentResponse;
import com.doritech.tmsservice.response.PageResponse;
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
public ResponseEntity createDirectTestAssignment(
        DirectTestAssignmentRequest request) {

    try {
        // 1. Validate request
        if (request == null) {
            return errorResponse(
                    "Direct Test assignment data is required",
                    HttpStatus.BAD_REQUEST);
        }

        Long testSetId = request.getTestSetId();
        Long requestedUserId = request.getUserId();
        Long batchId = request.getBatchId();

        if (testSetId == null || testSetId <= 0) {
            return errorResponse(
                    "Valid test set id is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestedUserId == null && batchId == null) {
            return errorResponse(
                    "Provide either userId or batchId",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestedUserId != null && requestedUserId <= 0) {
            return errorResponse(
                    "Valid user id is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (batchId != null && batchId <= 0) {
            return errorResponse(
                    "Valid batch id is required",
                    HttpStatus.BAD_REQUEST);
        }

        // 2. Find an active, published Direct Test Set
        TestSet testSet = testSetRepository
                .findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(
                        testSetId)
                .orElse(null);

        if (testSet == null) {
            return errorResponse(
                    "Published active Direct Test not found",
                    HttpStatus.NOT_FOUND);
        }

        // 3. Identify the logged-in assigning user
        Long currentUserId = CurrentUser.getUserId();

        if (currentUserId == null || currentUserId <= 0) {
            return errorResponse(
                    "Unable to identify current user",
                    HttpStatus.UNAUTHORIZED);
        }

        Integer currentUserIdInt;

        try {
            currentUserIdInt = Math.toIntExact(currentUserId);
        } catch (ArithmeticException ex) {
            return errorResponse(
                    "Invalid assigning user ID",
                    HttpStatus.BAD_REQUEST);
        }

        UserMaster assignedBy = userRepository
                .findById(currentUserIdInt)
                .orElse(null);

        if (assignedBy == null) {
            return errorResponse(
                    "Assigning user not found",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // 4. BATCH ASSIGNMENT: CREATE ONLY ONE DATABASE ROW
        // =====================================================

        if (batchId != null) {

            // Find the employees associated with the batch.
            List<UserBatch> batchMembers =
                    userBatchRepository.findAllByBatch_BatchId(batchId);

            if (batchMembers == null || batchMembers.isEmpty()) {
                return errorResponse(
                        "No employees found for the requested batch",
                        HttpStatus.NOT_FOUND);
            }

            Batch batch = batchMembers.get(0).getBatch();

            if (batch == null) {
                return errorResponse(
                        "Batch details not found",
                        HttpStatus.NOT_FOUND);
            }

            // Collect valid, distinct employee IDs.
            List<Long> targetUserIds = batchMembers.stream()
                    .map(UserBatch::getUserId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            if (targetUserIds.isEmpty()) {
                return errorResponse(
                        "No valid employees found for this batch",
                        HttpStatus.NOT_FOUND);
            }

            // If userId was also supplied, validate it.
            // It does not create a separate individual assignment.
            if (requestedUserId != null) {

                Integer requestedUserIdInt;

                try {
                    requestedUserIdInt =
                            Math.toIntExact(requestedUserId);
                } catch (ArithmeticException ex) {
                    return errorResponse(
                            "Invalid user id",
                            HttpStatus.BAD_REQUEST);
                }

                if (!userRepository.existsById(requestedUserIdInt)) {
                    return errorResponse(
                            "User not found",
                            HttpStatus.NOT_FOUND);
                }

                if (!targetUserIds.contains(requestedUserId)) {
                    return errorResponse(
                            "Selected user is not associated with this batch",
                            HttpStatus.BAD_REQUEST);
                }
            }

            // Do not create a second batch assignment for the same test.
            boolean batchAlreadyAssigned =
                    assignmentRepository
                            .existsByTestSet_TestSetIdAndBatch_BatchId(
                                    testSetId, batchId);

            if (batchAlreadyAssigned) {
                return errorResponse(
                        "This Direct Test is already assigned to this batch",
                        HttpStatus.CONFLICT);
            }

            // Create ONE assignment entity for the entire batch.
            DirectTestAssignment assignment =
                    buildAssignment(testSet, null, assignedBy);

            assignment.setTestSet(testSet);
            assignment.setUserId(null);
            assignment.setBatch(batch);
            assignment.setDueDate(request.getDueDate());

            // Save exactly one row.
            DirectTestAssignment savedAssignment =
                    assignmentRepository.saveAndFlush(assignment);

            // Return a one-element response list to preserve your
            // existing response structure.
            List<DirectTestAssignmentResponse> payload =
                    java.util.Collections.singletonList(
                            convertToResponse(savedAssignment));

            return new ResponseEntity(
                    "Direct Test assigned to batch successfully",
                    HttpStatus.CREATED.value(),
                    payload);
        }

        // =====================================================
        // 5. INDIVIDUAL ASSIGNMENT: CREATE ONE USER ROW
        // =====================================================

        Integer requestedUserIdInt;

        try {
            requestedUserIdInt =
                    Math.toIntExact(requestedUserId);
        } catch (ArithmeticException ex) {
            return errorResponse(
                    "Invalid user id",
                    HttpStatus.BAD_REQUEST);
        }

        if (!userRepository.existsById(requestedUserIdInt)) {
            return errorResponse(
                    "User not found",
                    HttpStatus.NOT_FOUND);
        }

        boolean alreadyAssigned =
                assignmentRepository
                        .existsByTestSet_TestSetIdAndUserId(
                                testSetId, requestedUserId);

        if (alreadyAssigned) {
            return errorResponse(
                    "This Direct Test is already assigned to the selected user",
                    HttpStatus.CONFLICT);
        }

        DirectTestAssignment assignment =
                buildAssignment(testSet, requestedUserId, assignedBy);

        assignment.setTestSet(testSet);
        assignment.setUserId(requestedUserId);
        assignment.setBatch(null);
        assignment.setDueDate(request.getDueDate());

        DirectTestAssignment savedAssignment =
                assignmentRepository.saveAndFlush(assignment);

        DirectTestAssignmentResponse payload =
                convertToResponse(savedAssignment);

        return new ResponseEntity(
                "Direct Test assigned to user successfully",
                HttpStatus.CREATED.value(),
                payload);

    } catch (DataIntegrityViolationException ex) {

        return errorResponse(
                "Direct Test assignment conflicts with an existing database record",
                HttpStatus.CONFLICT);

    } catch (Exception ex) {

        ex.printStackTrace();

        return errorResponse(
                "Failed to create Direct Test assignment",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

	// ================= CONVERT ENTITY TO RESPONSE =================

	private DirectTestAssignmentResponse convertToResponse(DirectTestAssignment assignment) {

		DirectTestAssignmentResponse response = new DirectTestAssignmentResponse();

		response.setDirectTestAssignmentId(assignment.getDirectTestAssignmentId());

		response.setTestSetId(assignment.getTestSet().getTestSetId());

		response.setTestName(assignment.getTestSet().getTestName());

		response.setAssignedBy(assignment.getAssignedBy());

		// ================= ASSIGNED BY USER DETAILS =================

		if (assignment.getAssignedBy() != null) {

			int assignedById = Math.toIntExact(assignment.getAssignedBy());

			if (userRepository.findById(assignedById).isPresent()) {
				response.setAssignedByName(userRepository.findEmployeeNameByUserId(assignedById));
			}
		}

		// ================= USER AND BATCH DETAILS =================

		if (assignment.getBatch() != null) {

			// Batch assignment: return batch details
			response.setBatchId(assignment.getBatch().getBatchId());

			response.setBatchName(assignment.getBatch().getBatchName());

			// Hide individual user details in the response
			response.setUserId(null);
			response.setUserName(null);

		} else {

			// Individual assignment: return user details
			response.setBatchId(null);
			response.setBatchName(null);

			response.setUserId(assignment.getUserId());

			if (assignment.getUserId() != null) {

				int userId = Math.toIntExact(assignment.getUserId());

				if (userRepository.findById(userId).isPresent()) {
					response.setUserName(userRepository.findEmployeeNameByUserId(userId));
				}
			}
		}

		// ================= ASSIGNMENT DETAILS =================

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

	// ================= GET MY DIRECT TEST SETS BY CURRENT USER =================

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getMyDirectTestSets() {

		Long currentUserId = CurrentUser.getUserId();

		try {
			if (currentUserId == null || currentUserId <= 0) {
				return errorResponse("Unable to identify current user", HttpStatus.UNAUTHORIZED);
			}

			int currentUserIdInt = Math.toIntExact(currentUserId);
			String currentUserName = userRepository.findEmployeeNameByUserId(currentUserIdInt);

			// 1. Assignments that belong to this user (individual + batch rows created for
			// this user)
			List<DirectTestAssignment> assignments = new ArrayList<>(assignmentRepository.findByUserId(currentUserId));

			// Test sets the user already has
			Set<Long> coveredTestSetIds = assignments.stream().map(a -> a.getTestSet().getTestSetId())
					.collect(Collectors.toCollection(HashSet::new));

			// 2. Batches the user belongs to (checked from user_batch)
			List<Long> batchIds = userBatchRepository.findAllByUserId(currentUserId).stream()
					.filter(ub -> ub.getBatch() != null).map(ub -> ub.getBatch().getBatchId()).distinct()
					.collect(Collectors.toList());

			// 3. Add batch assignments only for test sets the user doesn't already have
			// (one per test set)
			if (!batchIds.isEmpty()) {
				for (DirectTestAssignment batchAssignment : assignmentRepository.findByBatch_BatchIdIn(batchIds)) {
					Long testSetId = batchAssignment.getTestSet().getTestSetId();
					if (coveredTestSetIds.add(testSetId)) { // false if already present
						assignments.add(batchAssignment);
					}
				}
			}

			// 4. Convert, and show the current user's id and name in every row
			List<DirectTestAssignmentResponse> payload = assignments.stream().map(this::convertToResponse).peek(r -> {
				r.setUserId(currentUserId);
				r.setUserName(currentUserName);
			}).collect(Collectors.toList());

			return new ResponseEntity(
					"Direct Current test assignments fetched successfully with current id" + currentUserId,
					HttpStatus.OK.value(), payload);

		} catch (Exception e) {
			e.printStackTrace();
			return errorResponse("Failed to fetch direct test sets with current id", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ======================== GET ALL DIRECT TEST SET ========================

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllDirectTestSet(int page, int size) {

		try {
			Pageable pageable = PageRequest.of(page, size);

			Page<DirectTestAssignment> assignmentPage = assignmentRepository.findAll(pageable);

			// 2. Remove duplicates and convert to response DTO
			List<DirectTestAssignmentResponse> payload = assignmentPage.getContent().stream()
					.collect(Collectors.toMap(DirectTestAssignment::getDirectTestAssignmentId, assignment -> assignment,
							(existing, duplicate) -> existing, LinkedHashMap::new))
					.values().stream().map(this::convertToResponse).collect(Collectors.toList());

			// 3. Create pagination response
			PageResponse<DirectTestAssignmentResponse> pageResponse = new PageResponse<>(payload,
					assignmentPage.getNumber(), assignmentPage.getSize(), assignmentPage.getTotalElements(),
					assignmentPage.getTotalPages(), assignmentPage.isLast());

			// 4. Return success response
			return new ResponseEntity("Direct test assignments fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (Exception e) {
			e.printStackTrace();

			return errorResponse("Failed to fetch direct test assignments", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

// This API is working fine but in this api you need to map an condition that is if i have assigned a test set to a batch and current user is 
//	mapped with that batch then show that test set to current user also and also to check that is user is associated with that batch or not you need to 
//  check from user batch

	// ================= COMMON ERROR RESPONSE =================

	private ResponseEntity errorResponse(String message, HttpStatus status) {

		return new ResponseEntity(message, status.value(), null);
	}

	// ================= GET DIRECT TEST SET BY ID =================

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getDirectTestSetsById(Long id) {

		try {
			if (id == null || id <= 0) {
				return errorResponse("Invalid direct test assignment ID", HttpStatus.BAD_REQUEST);
			}

			// Fetch assignment by primary key
			Optional<DirectTestAssignment> optionalAssignment = assignmentRepository.findById(id);

			if (optionalAssignment.isEmpty()) {
				return errorResponse("Direct test assignment not found with ID: " + id, HttpStatus.NOT_FOUND);
			}

			// Convert entity into response DTO
			DirectTestAssignmentResponse payload = convertToResponse(optionalAssignment.get());

			return new ResponseEntity("Direct test assignment fetched successfully", HttpStatus.OK.value(), payload);

		} catch (Exception e) {
			e.printStackTrace();

			return errorResponse("Failed to fetch direct test assignment", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ================ Delete direct test set assignment ====================
	@Override
	@Transactional(value = "tmsTransactionManager")
	public ResponseEntity deleteById(Long id) {
		try {
			Optional<DirectTestAssignment> optional = assignmentRepository.findById(id);

			if (optional.isEmpty()) {
				return errorResponse("Direct test assignment not found with ID: " + id, HttpStatus.NOT_FOUND);
			}

			assignmentRepository.delete(optional.get());

			return new ResponseEntity("Direct test assignment deleted successfully with the id :" + id,
					HttpStatus.OK.value(), null);

		} catch (Exception e) {
			e.printStackTrace();
			return errorResponse("Failed to delete direct test assignment", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ================= UPDATE DIRECT TEST SET ASSIGNMENT ===============

//	@Override
//	@Transactional("tmsTransactionManager")
//	public ResponseEntity updateDirectTestAssignment(Long id, DirectTestAssignmentRequest request) {
//
//		try {
//			// 1. Validate request before accessing it
//			if (id == null || id <= 0) {
//				return errorResponse("Valid Direct Test Assignment ID is required", HttpStatus.BAD_REQUEST);
//			}
//
//			if (request == null) {
//				return errorResponse("Data is required to update the Direct Test assignment", HttpStatus.BAD_REQUEST);
//			}
//
//			// 2. Fetch the exact assignment requested in the URL
//			Optional<DirectTestAssignment> optionalAssignment = assignmentRepository.findById(id);
//
//			if (optionalAssignment.isEmpty()) {
//				return errorResponse("Direct Test assignment not found with ID: " + id, HttpStatus.NOT_FOUND);
//			}
//
//			DirectTestAssignment assignment = optionalAssignment.get();
//
//			System.out.println("Requested assignment ID: " + assignment.getDirectTestAssignmentId());
//
//			// 3. Resolve the test set
//			TestSet testSet = assignment.getTestSet();
//
//			if (request.getTestSetId() != null) {
//
//				Long requestedTestSetId = request.getTestSetId();
//
//				if (requestedTestSetId <= 0) {
//					return errorResponse("Valid test set ID is required", HttpStatus.BAD_REQUEST);
//				}
//
//				testSet = testSetRepository
//						.findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(requestedTestSetId)
//						.orElse(null);
//
//				if (testSet == null) {
//					return errorResponse("Published active Direct Test not found", HttpStatus.NOT_FOUND);
//				}
//			}
//
//			if (testSet == null) {
//				return errorResponse("Test set is not associated with this assignment", HttpStatus.NOT_FOUND);
//			}
//
//			Long finalTestSetId = testSet.getTestSetId();
//
//			// =====================================================
//			// 4. BATCH UPDATE
//			// =====================================================
//
//			if (request.getBatchId() != null) {
//
//				Long requestedBatchId = request.getBatchId();
//
//				if (requestedBatchId <= 0) {
//					return errorResponse("Valid batch ID is required", HttpStatus.BAD_REQUEST);
//				}
//
//				// 5. Fetch members of the requested batch
//				List<UserBatch> batchMembers = userBatchRepository.findAllByBatch_BatchId(requestedBatchId);
//
//				if (batchMembers == null || batchMembers.isEmpty()) {
//					return errorResponse("No employees found for the requested batch", HttpStatus.NOT_FOUND);
//				}
//
//				Batch batch = batchMembers.get(0).getBatch();
//
//				if (batch == null) {
//					return errorResponse("Batch details not found", HttpStatus.NOT_FOUND);
//				}
//
//				// 6. Extract distinct employee IDs
//				List<Long> targetUserIds = batchMembers.stream().map(UserBatch::getUserId)
//						.filter(java.util.Objects::nonNull).distinct().collect(java.util.stream.Collectors.toList());
//
//				if (targetUserIds.isEmpty()) {
//					return errorResponse("No valid employees found for this batch", HttpStatus.NOT_FOUND);
//				}
//
//				// Optional userId must belong to the selected batch.
//				// The batch branch still processes all batch members.
//				if (request.getUserId() != null) {
//
//					if (request.getUserId() <= 0) {
//						return errorResponse("Valid user ID is required", HttpStatus.BAD_REQUEST);
//					}
//
//					if (!targetUserIds.contains(request.getUserId())) {
//						return errorResponse("Selected user is not associated with this batch", HttpStatus.BAD_REQUEST);
//					}
//				}
//
//				// 7. Validate the assigning user
//				Integer assignedById;
//
//				try {
//					assignedById = Math.toIntExact(assignment.getAssignedBy());
//				} catch (ArithmeticException | NullPointerException ex) {
//					return errorResponse("Invalid assigning user ID", HttpStatus.BAD_REQUEST);
//				}
//
//				UserMaster assignedBy = userRepository.findById(assignedById).orElse(null);
//
//				if (assignedBy == null) {
//					return errorResponse("Assigning user not found", HttpStatus.NOT_FOUND);
//				}
//
//				// 8. Load existing assignments.
//				// The requested assignment is explicitly prioritized below.
//				List<DirectTestAssignment> allAssignments = assignmentRepository.findAll();
//
//				List<DirectTestAssignment> assignmentsToSave = new java.util.ArrayList<>();
//
//				java.util.Set<Long> processedUserIds = new java.util.HashSet<>();
//
//				boolean selectedAssignmentUsed = false;
//
//				// Process the employee already associated with the requested
//				// assignment first, if that employee belongs to this batch.
//				List<Long> orderedUserIds = new java.util.ArrayList<>(targetUserIds);
//
//				Long selectedUserId = assignment.getUserId();
//
//				if (selectedUserId != null && orderedUserIds.contains(selectedUserId)) {
//
//					orderedUserIds.remove(selectedUserId);
//					orderedUserIds.add(0, selectedUserId);
//				}
//
//				// 9. Prepare each employee's assignment
//				for (Long memberUserId : orderedUserIds) {
//
//					Integer memberId;
//
//					try {
//						memberId = Math.toIntExact(memberUserId);
//					} catch (ArithmeticException ex) {
//						return errorResponse("Invalid user ID in batch", HttpStatus.BAD_REQUEST);
//					}
//
//					if (!userRepository.existsById(memberId)) {
//						continue;
//					}
//
//					// First, find an assignment for this test and employee.
//					DirectTestAssignment existingAssignment = allAssignments.stream()
//							.filter(existing -> existing.getTestSet() != null
//									&& existing.getTestSet().getTestSetId().equals(finalTestSetId)
//									&& memberUserId.equals(existing.getUserId()))
//							.findFirst().orElse(null);
//
//					DirectTestAssignment memberAssignment;
//
//					// IMPORTANT:
//					// If this is the employee currently associated with the
//					// URL assignment, preserve that exact assignment ID.
//					if (memberUserId.equals(selectedUserId)) {
//
//						// Do not overwrite another record when the requested
//						// test/user combination already belongs to another ID.
//						if (existingAssignment != null && !existingAssignment.getDirectTestAssignmentId().equals(id)) {
//
//							return errorResponse("Another assignment already exists for this "
//									+ "test set and employee. Resolve the "
//									+ "duplicate before updating assignment ID: " + id, HttpStatus.CONFLICT);
//						}
//
//						memberAssignment = assignment;
//						selectedAssignmentUsed = true;
//
//					} else if (existingAssignment != null) {
//
//						// Update the employee's existing assignment.
//						memberAssignment = existingAssignment;
//
//					} else if (!selectedAssignmentUsed) {
//
//						// Reuse the URL assignment only when its current user
//						// is not in the batch and it has not been used yet.
//						memberAssignment = assignment;
//						selectedAssignmentUsed = true;
//
//					} else {
//
//						// Create an assignment for another employee.
//						memberAssignment = buildAssignment(testSet, memberUserId, assignedBy);
//					}
//
//					// 10. Update assignment fields
//					memberAssignment.setTestSet(testSet);
//					memberAssignment.setUserId(memberUserId);
//					memberAssignment.setBatch(batch);
//
//					if (request.getDueDate() != null) {
//						memberAssignment.setDueDate(request.getDueDate());
//					}
//
//					assignmentsToSave.add(memberAssignment);
//					processedUserIds.add(memberUserId);
//				}
//
//				if (assignmentsToSave.isEmpty()) {
//					return errorResponse("No valid employees found for this batch", HttpStatus.NOT_FOUND);
//				}
//
//				// 11. If the requested assignment was not reused, do not
//				// silently return a response containing only other IDs.
//				if (!selectedAssignmentUsed) {
//					return errorResponse("The requested assignment could not be included " + "in this batch update",
//							HttpStatus.CONFLICT);
//				}
//
//				// 12. Save all batch assignments
//				List<DirectTestAssignment> savedAssignments = assignmentRepository.saveAllAndFlush(assignmentsToSave);
//
//				// 13. Return the saved records
//				List<DirectTestAssignmentResponse> payload = savedAssignments.stream().map(this::convertToResponse)
//						.collect(java.util.stream.Collectors.toList());
//
//				return new ResponseEntity("Direct Test batch assignments updated successfully", HttpStatus.OK.value(),
//						payload);
//			}
//
//			// =====================================================
//			// 14. INDIVIDUAL ASSIGNMENT UPDATE
//			// =====================================================
//
//			Long userId = request.getUserId() != null ? request.getUserId() : assignment.getUserId();
//
//			if (userId == null || userId <= 0) {
//				return errorResponse("Valid user ID is required", HttpStatus.BAD_REQUEST);
//			}
//
//			Integer userIdInt;
//
//			try {
//				userIdInt = Math.toIntExact(userId);
//			} catch (ArithmeticException ex) {
//				return errorResponse("Invalid user ID", HttpStatus.BAD_REQUEST);
//			}
//
//			if (!userRepository.existsById(userIdInt)) {
//				return errorResponse("User not found", HttpStatus.NOT_FOUND);
//			}
//
//			// 15. Prevent duplicate test-set assignments
//			boolean duplicateExists = assignmentRepository
//					.existsByTestSet_TestSetIdAndUserIdAndDirectTestAssignmentIdNot(finalTestSetId, userId, id);
//
//			if (duplicateExists) {
//				return errorResponse("This test is already assigned to the selected user", HttpStatus.CONFLICT);
//			}
//
//			assignment.setTestSet(testSet);
//			assignment.setUserId(userId);
//			assignment.setBatch(null);
//
//			if (request.getDueDate() != null) {
//				assignment.setDueDate(request.getDueDate());
//			}
//
//			// 16. Save the exact requested assignment
//			DirectTestAssignment updatedAssignment = assignmentRepository.saveAndFlush(assignment);
//
//			DirectTestAssignmentResponse payload = convertToResponse(updatedAssignment);
//
//			return new ResponseEntity("Direct Test assignment updated successfully", HttpStatus.OK.value(), payload);
//
//		} catch (DataIntegrityViolationException ex) {
//
//			return errorResponse("Direct Test assignment conflicts with an existing database record",
//					HttpStatus.CONFLICT);
//
//		} catch (Exception ex) {
//
//			ex.printStackTrace();
//
//			return errorResponse("Failed to update Direct Test assignment", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}


@Override
@Transactional("tmsTransactionManager")
public ResponseEntity updateDirectTestAssignment(
        Long id,
        DirectTestAssignmentRequest request) {

    try {
        // 1. Validate request
        if (id == null || id <= 0) {
            return errorResponse(
                    "Valid direct test assignment ID is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (request == null || request.getTestSetId() == null
                || request.getTestSetId() <= 0) {
            return errorResponse(
                    "Valid test set ID is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (request.getBatchId() == null && request.getUserId() == null) {
            return errorResponse(
                    "Either userId or batchId is required",
                    HttpStatus.BAD_REQUEST);
        }

        // 2. Find the existing assignment
        DirectTestAssignment assignment =
                assignmentRepository.findById(id).orElse(null);

        if (assignment == null) {
            return errorResponse(
                    "Direct test assignment not found with ID: " + id,
                    HttpStatus.NOT_FOUND);
        }

        // 3. Validate published, active Direct Test Set
        TestSet testSet = testSetRepository
                .findByTestSetIdAndTrainingIsNullAndIsActiveTrueAndPublishedAtIsNotNull(
                        request.getTestSetId())
                .orElse(null);

        if (testSet == null) {
            return errorResponse(
                    "Published active Direct Test not found",
                    HttpStatus.NOT_FOUND);
        }

        // 4. Update as a batch assignment
        if (request.getBatchId() != null) {

            Long batchId = request.getBatchId();

            if (batchId <= 0) {
                return errorResponse(
                        "Valid batch ID is required",
                        HttpStatus.BAD_REQUEST);
            }

            // Find batch using its members
            List<UserBatch> batchMembers =
                    userBatchRepository.findAllByBatch_BatchId(batchId);

            if (batchMembers == null || batchMembers.isEmpty()) {
                return errorResponse(
                        "No employees found for the requested batch",
                        HttpStatus.NOT_FOUND);
            }

            Batch batch = batchMembers.get(0).getBatch();

            if (batch == null) {
                return errorResponse(
                        "Batch not found with ID: " + batchId,
                        HttpStatus.NOT_FOUND);
            }

            // Optional: if userId is also supplied, ensure that user
            // belongs to the requested batch.
            if (request.getUserId() != null) {
                boolean userBelongsToBatch = batchMembers.stream()
                        .anyMatch(ub ->
                                ub.getUserId() != null
                                && ub.getUserId().longValue()
                                        == request.getUserId());

                if (!userBelongsToBatch) {
                    return errorResponse(
                            "The requested user does not belong to this batch",
                            HttpStatus.BAD_REQUEST);
                }
            }

            // Prevent duplicate assignment of the same test to the batch.
            boolean duplicateBatchAssignment =
                    assignmentRepository
                            .existsByTestSet_TestSetIdAndBatch_BatchIdAndDirectTestAssignmentIdNot(
                                    request.getTestSetId(),
                                    batchId,
                                    id);

            if (duplicateBatchAssignment) {
                return errorResponse(
                        "This Direct Test is already assigned to the batch",
                        HttpStatus.CONFLICT);
            }

            // Update the same row. Do not create one row per employee.
            assignment.setTestSet(testSet);
            assignment.setBatch(batch);
            assignment.setUserId(null);
            assignment.setDueDate(request.getDueDate());

        } else {
            // 5. Update as an individual assignment
            Long userId = request.getUserId();

            if (userId <= 0) {
                return errorResponse(
                        "Valid user ID is required",
                        HttpStatus.BAD_REQUEST);
            }

            boolean duplicateUserAssignment =
                    assignmentRepository
                            .existsByTestSet_TestSetIdAndUserIdAndDirectTestAssignmentIdNot(
                                    request.getTestSetId(),
                                    userId,
                                    id);

            if (duplicateUserAssignment) {
                return errorResponse(
                        "This Direct Test is already assigned to the user",
                        HttpStatus.CONFLICT);
            }

            assignment.setTestSet(testSet);
            assignment.setUserId(userId);
            assignment.setBatch(null);
            assignment.setDueDate(request.getDueDate());
        }

        // 6. Save the existing record
        DirectTestAssignment updated =
                assignmentRepository.saveAndFlush(assignment);

        // 7. Return the updated record
        return new ResponseEntity(
                "Direct Test assignment updated successfully",
                HttpStatus.OK.value(),
                convertToResponse(updated));

    } catch (org.springframework.dao.DataIntegrityViolationException e) {
        return errorResponse(
                "Duplicate assignment or database constraint violation",
                HttpStatus.CONFLICT);

    } catch (Exception e) {
        e.printStackTrace();
        return errorResponse(
                "Failed to update Direct Test assignment: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}