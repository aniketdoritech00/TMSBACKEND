package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.request.UserBatchRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.UserBatchResponse;
import com.doritech.tmsservice.service.UserBatchService;
import com.doritech.tmsservice.tms.entity.Batch;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.UserBatch;
import com.doritech.tmsservice.tms.repository.BatchRepository;
import com.doritech.tmsservice.tms.repository.UserBatchRepository;

@Service
public class UserBatchServiceImpl implements UserBatchService {

	private final UserBatchRepository userBatchRepository;
	private final BatchRepository batchRepository;

	public UserBatchServiceImpl(UserBatchRepository userBatchRepository, BatchRepository batchRepository) {

		this.userBatchRepository = userBatchRepository;
		this.batchRepository = batchRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity assignBatchToUser(UserBatchRequest request) {

	    Long currentUserId = CurrentUser.getUserId();

	    ResponseEntity response = new ResponseEntity();

	    if (request == null) {
	        response.setMessage("Request is null!");
	        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        response.setPayload(null);
	        return response;
	    }

	    try {

	        // Validate user IDs
	        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {

	            response.setMessage("User IDs are required!");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        // Validate batch ID
	        if (request.getBatchId() == null) {

	            response.setMessage("Batch ID is required!");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        // Check batch
	        Optional<Batch> optionalBatch =
	                batchRepository.findById(request.getBatchId());

	        if (optionalBatch.isEmpty()) {

	            response.setMessage("Batch not found!");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            response.setPayload(null);
	            return response;
	        }

	        Batch batch = optionalBatch.get();

	        List<UserBatchResponse> assignedUsers = new ArrayList<>();
	        List<Long> alreadyAssignedUsers = new ArrayList<>();

	        for (Long userId : request.getUserIds()) {

	            if (userId == null || userId <= 0) {
	                continue;
	            }

	            // Check duplicate assignment
	            if (userBatchRepository.existsByUserIdAndBatch_BatchId(
	                    userId, request.getBatchId())) {

	                alreadyAssignedUsers.add(userId);
	                continue;
	            }

	            UserBatch userBatch = convertToEntity(request, batch, userId);

	            userBatch.setUserId(userId);
	            userBatch.setAssignedBy(currentUserId);
	            userBatch.setAssignedAt(LocalDateTime.now());

	            UserBatch savedUserBatch =
	                    userBatchRepository.save(userBatch);

	            UserBatchResponse userBatchResponse =
	                    convertToResponse(savedUserBatch);

	            assignedUsers.add(userBatchResponse);
	        }

	        // Nothing was assigned
	        if (assignedUsers.isEmpty()) {

	            response.setMessage(
	                    "All selected users are already assigned to this batch!");

	            response.setStatusCode(HttpStatus.CONFLICT.value());
	            response.setPayload(alreadyAssignedUsers);

	            return response;
	        }

	        // Some users were assigned, some were already assigned
	        if (!alreadyAssignedUsers.isEmpty()) {

	            response.setMessage(
	                    "Batch assigned successfully to new users. Some users were already assigned.");

	            response.setStatusCode(HttpStatus.CREATED.value());

	            response.setPayload(assignedUsers);

	            return response;
	        }

	        // All users successfully assigned
	        response.setMessage(
	                "Batch assigned to all users successfully!");

	        response.setStatusCode(HttpStatus.CREATED.value());
	        response.setPayload(assignedUsers);

	    } catch (DataIntegrityViolationException e) {

	        e.printStackTrace();

	        response.setMessage(
	                "Batch assignment already exists or violates database constraints!");

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
	public ResponseEntity updateUserBatch(Long userBatchId, UserBatchRequest request) {

	    ResponseEntity response = new ResponseEntity();

	    if (request == null) {
	        response.setMessage("Request is null!");
	        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        response.setPayload(null);
	        return response;
	    }

	    try {

	        if (userBatchId == null || userBatchId <= 0) {
	            response.setMessage("Valid User Batch ID is required!");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
	            response.setMessage("User ID is required!");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        if (request.getUserIds().size() != 1) {
	            response.setMessage(
	                    "Exactly one User ID is required while updating a user batch assignment!"
	            );
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        Long userId = request.getUserIds().get(0);

	        if (userId == null || userId <= 0) {
	            response.setMessage("Valid User ID is required!");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        if (request.getBatchId() == null || request.getBatchId() <= 0) {
	            response.setMessage("Valid Batch ID is required!");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        Optional<UserBatch> optionalUserBatch =
	                userBatchRepository.findById(userBatchId);

	        if (optionalUserBatch.isEmpty()) {
	            response.setMessage("User batch assignment not found!");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            response.setPayload(null);
	            return response;
	        }

	        UserBatch userBatch = optionalUserBatch.get();

	        Optional<Batch> optionalBatch =
	                batchRepository.findById(request.getBatchId());

	        if (optionalBatch.isEmpty()) {
	            response.setMessage("Batch not found!");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            response.setPayload(null);
	            return response;
	        }

	        /*
	         * Check whether another UserBatch record already
	         * exists for the same user and batch.
	         */
	        Optional<UserBatch> duplicateAssignment =
	                userBatchRepository.findByUserIdAndBatch_BatchId(
	                        userId,
	                        request.getBatchId()
	                );

	        if (duplicateAssignment.isPresent()
	                && !duplicateAssignment.get()
	                        .getUserBatchId()
	                        .equals(userBatchId)) {

	            response.setMessage(
	                    "Batch is already assigned to this user!"
	            );
	            response.setStatusCode(HttpStatus.CONFLICT.value());
	            response.setPayload(null);
	            return response;
	        }

	        /*
	         * Update existing assignment
	         */
	        userBatch.setUserId(userId);
	        userBatch.setBatch(optionalBatch.get());

	        /*
	         * Keep original assignment date.
	         * No need to call setAssignedAt() because
	         * it is already present in the existing entity.
	         */

	        UserBatch updatedUserBatch =
	                userBatchRepository.save(userBatch);

	        UserBatchResponse userBatchResponse =
	                convertToResponse(updatedUserBatch);

	        response.setMessage(
	                "User batch assignment updated successfully!"
	        );
	        response.setStatusCode(HttpStatus.OK.value());
	        response.setPayload(userBatchResponse);

	    } catch (DataIntegrityViolationException e) {

	        e.printStackTrace();

	        response.setMessage(
	                "Batch assignment violates database constraints!"
	        );
	        response.setStatusCode(HttpStatus.CONFLICT.value());
	        response.setPayload(null);

	    } catch (Exception e) {

	        e.printStackTrace();

	        response.setMessage("Internal server error!");
	        response.setStatusCode(
	                HttpStatus.INTERNAL_SERVER_ERROR.value()
	        );
	        response.setPayload(null);
	    }

	    return response;
	}

	@Override
	public ResponseEntity getAllUserBatches(int page, int size) {

	    ResponseEntity response = new ResponseEntity();

	    try {

	        if (page < 0) {
	            response.setMessage("Page number cannot be negative");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        if (size <= 0) {
	            response.setMessage("Page size must be greater than 0");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        Pageable pageable = PageRequest.of(page, size);

	        Page<UserBatch> userBatchPage =
	                userBatchRepository.findAll(pageable);

	        if (userBatchPage.isEmpty()) {

	            response.setMessage("No user batch assignments found!");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            response.setPayload(null);

	            return response;
	        }

	        List<UserBatchResponse> userBatchResponses =
	                userBatchPage.getContent()
	                        .stream()
	                        .map(this::convertToResponse)
	                        .toList();

	        PageResponse<UserBatchResponse> pageResponse =
	                new PageResponse<>();

	        pageResponse.setContent(userBatchResponses);
	        pageResponse.setPageNumber(userBatchPage.getNumber());
	        pageResponse.setPageSize(userBatchPage.getSize());
	        pageResponse.setTotalElements(userBatchPage.getTotalElements());
	        pageResponse.setTotalPages(userBatchPage.getTotalPages());
	        pageResponse.setLastPage(userBatchPage.isLast());

	        response.setMessage("User batch assignments found successfully!");
	        response.setStatusCode(HttpStatus.OK.value());
	        response.setPayload(pageResponse);

	    } catch (Exception e) {

	        e.printStackTrace();

	        response.setMessage("Internal server error!");
	        response.setStatusCode(
	                HttpStatus.INTERNAL_SERVER_ERROR.value()
	        );
	        response.setPayload(null);
	    }

	    return response;
	}


	@Override
	public ResponseEntity getUserBatchById(Long userBatchId) {
		ResponseEntity response = new ResponseEntity();

		try {
			if (userBatchId == null) {
				response.setMessage("User Batch ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);

				return response;
			}

			Optional<UserBatch> optionalUserBatch = userBatchRepository.findById(userBatchId);

			if (optionalUserBatch.isEmpty()) {
				response.setMessage("User batch assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);

				return response;
			}

			UserBatchResponse userBatchResponse = convertToResponse(optionalUserBatch.get());
			response.setMessage("User batch assignment found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(userBatchResponse);

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity deleteUserBatchById(Long userBatchId) {
		ResponseEntity response = new ResponseEntity();

		try {

			if (userBatchId == null) {
				response.setMessage("User Batch ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);

				return response;
			}

			if (!userBatchRepository.existsById(userBatchId)) {
				response.setMessage("User batch assignment not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);

				return response;
			}

			userBatchRepository.deleteById(userBatchId);

			response.setMessage("User batch assignment deleted successfully!");

			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(null);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			response.setMessage("Unable to delete user batch assignment because it is being used!");
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

	private UserBatch convertToEntity(UserBatchRequest request, Batch batch, Long userId) {

	    UserBatch userBatch = new UserBatch();

	    userBatch.setUserId(userId);
	    userBatch.setBatch(batch);

	    return userBatch;
	}

	private UserBatchResponse convertToResponse(UserBatch userBatch) {
		UserBatchResponse response = new UserBatchResponse();
		response.setUserBatchId(userBatch.getUserBatchId());
		response.setUserId(userBatch.getUserId());
		if (userBatch.getBatch() != null) {
			response.setBatchId(userBatch.getBatch().getBatchId());
		}
		response.setAssignedAt(userBatch.getAssignedAt());
		response.setAssignedBy(userBatch.getAssignedBy());
		return response;
	}
}