package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
			if (request.getUserId() == null) {
				response.setMessage("User ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);

				return response;
			}
			if (request.getBatchId() == null) {
				response.setMessage("Batch ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			Optional<Batch> optionalBatch = batchRepository.findById(request.getBatchId());

			if (optionalBatch.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}
			
			if (userBatchRepository.existsByUserIdAndBatch_BatchId(request.getUserId(), request.getBatchId())) {
				response.setMessage("Batch is already assigned to this user!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);

				return response;
			}

			UserBatch userBatch = convertToEntity(request, optionalBatch.get());
			userBatch.setAssignedBy(currentUserId);
			userBatch.setAssignedAt(LocalDateTime.now());

			UserBatch savedUserBatch = userBatchRepository.save(userBatch);

			UserBatchResponse userBatchResponse = convertToResponse(savedUserBatch);

			response.setMessage("Batch assigned to user successfully!");
			response.setStatusCode(HttpStatus.CREATED.value());
			response.setPayload(userBatchResponse);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();

			response.setMessage("Batch assignment already exists or violates database constraints!");
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
			if (userBatchId == null) {
				response.setMessage("User Batch ID is required!");
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

			if (request.getBatchId() == null) {
				response.setMessage("Batch ID is required!");
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
			Optional<Batch> optionalBatch = batchRepository.findById(request.getBatchId());
			if (optionalBatch.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);

				return response;
			}
			Optional<UserBatch> duplicateAssignment = userBatchRepository
					.findByUserIdAndBatch_BatchId(request.getUserId(), request.getBatchId());

			if (duplicateAssignment.isPresent() && !duplicateAssignment.get().getUserBatchId().equals(userBatchId)) {
				response.setMessage("Batch is already assigned to this user!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);

				return response;
			}

			UserBatch userBatch = optionalUserBatch.get();

			userBatch.setUserId(request.getUserId());
			userBatch.setBatch(optionalBatch.get());
			userBatch.setAssignedAt(optionalUserBatch.get().getAssignedAt());
			UserBatch updatedUserBatch = userBatchRepository.save(userBatch);

			UserBatchResponse userBatchResponse = convertToResponse(updatedUserBatch);

			response.setMessage("User batch assignment updated successfully!");

			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(userBatchResponse);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			response.setMessage("Batch assignment violates database constraints!");
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

	private UserBatch convertToEntity(UserBatchRequest request, Batch batch) {
		UserBatch userBatch = new UserBatch();
		userBatch.setUserId(request.getUserId());
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