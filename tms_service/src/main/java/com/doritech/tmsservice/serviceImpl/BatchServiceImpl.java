package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.request.BatchRequest;
import com.doritech.tmsservice.response.BatchResponse;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.service.BatchService;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.tms.entity.Batch;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.repository.BatchRepository;
import com.doritech.tmsservice.tms.repository.UserBatchRepository;

@Service
public class BatchServiceImpl implements BatchService {

	private final BatchRepository batchRepository;
	private final ParamService paramService;
	private final UserBatchRepository userBatchRepository;

	public BatchServiceImpl(BatchRepository batchRepository, ParamService paramService,
			UserBatchRepository userBatchRepository) {
		this.batchRepository = batchRepository;
		this.paramService = paramService;
		this.userBatchRepository = userBatchRepository;
	}

	@Override
	public ResponseEntity createBatch(BatchRequest request) {
		Long currentUserId = CurrentUser.getUserId();
		ResponseEntity response = new ResponseEntity();
		try {

			if (batchRepository.findByBatchName(request.getBatchName()).isPresent()) {
				response.setMessage("Batch name already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);

				return response;
			}
			if (request.getBatchCode() != null && batchRepository.findByBatchCode(request.getBatchCode()).isPresent()) {

				response.setMessage("Batch code already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);

				return response;
			}

			Batch batch = convertToEntity(request);
			batch.setCreatedBy(currentUserId);
			Batch savedBatch = batchRepository.save(batch);

			paramService.updateCodeValue(savedBatch.getBatchCode());
			BatchResponse batchResponse = convertToResponse(savedBatch);

			response.setMessage("Batch created successfully!");
			response.setStatusCode(HttpStatus.CREATED.value());
			response.setPayload(batchResponse);

		} catch (DataIntegrityViolationException e) {

			response.setMessage("Batch name or batch code already exists!");
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setPayload(null);

		} catch (Exception e) {

			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity updateBatch(Long batchId, BatchRequest request) {

		Long currentUserId = CurrentUser.getUserId();
		ResponseEntity response = new ResponseEntity();

		if (request == null) {
			response.setMessage("Request is null");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setPayload(null);
			return response;
		}

		try {

			Optional<Batch> optionalBatch = batchRepository.findById(batchId);

			if (optionalBatch.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			if (batchRepository.findByBatchNameAndBatchIdNot(request.getBatchName(), batchId).isPresent()) {
				response.setMessage("Batch name already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);
				return response;
			}

			Batch batch = optionalBatch.get();

			batch.setBatchName(request.getBatchName());
			batch.setBatchDescription(request.getBatchDescription());
			batch.setUpdatedBy(currentUserId);
			batch.setUpdatedAt(LocalDateTime.now());
			Batch updatedBatch = batchRepository.save(batch);

			BatchResponse batchResponse = convertToResponse(updatedBatch);

			response.setMessage("Batch updated successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(batchResponse);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			response.setMessage("Batch name already exists!");
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setPayload(null);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Error while updating batch");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity getAllBatch(int page, int size) {

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

			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "batchId"));

			Page<Batch> batchPage = batchRepository.findAll(pageable);

			if (batchPage.isEmpty()) {
				response.setMessage("No batches found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			List<BatchResponse> batchResponses = batchPage.getContent().stream().map(this::convertToResponse).toList();

			PageResponse<BatchResponse> pageResponse = new PageResponse<>();

			pageResponse.setContent(batchResponses);
			pageResponse.setPageNumber(batchPage.getNumber());
			pageResponse.setPageSize(batchPage.getSize());
			pageResponse.setTotalElements(batchPage.getTotalElements());
			pageResponse.setTotalPages(batchPage.getTotalPages());
			pageResponse.setLastPage(batchPage.isLast());

			response.setMessage("Batches found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity getBatchbyId(Long batchId) {
		ResponseEntity response = new ResponseEntity();
		try {

			Optional<Batch> optionalBatch = batchRepository.findById(batchId);

			if (optionalBatch.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			BatchResponse batchResponse = convertToResponse(optionalBatch.get());

			response.setMessage("Batch found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(batchResponse);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	@Override
	public ResponseEntity deleteBatchbyId(Long batchId) {
		ResponseEntity response = new ResponseEntity();
		try {
			if (batchId == null) {
				response.setMessage("Batch ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}
			Optional<Batch> batchOptional = batchRepository.findById(batchId);

			if (batchOptional.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			Batch batch = batchOptional.get();

			if (userBatchRepository.existsByBatch_BatchId(batchId)) {
				response.setMessage("Batch cannot be deleted because it's associated with another child table!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);
				return response;
			}

			String batchCode = batch.getBatchCode();

			batchRepository.delete(batch);

			paramService.updateCodeValueOnDelete(batchCode);

			response.setMessage("Batch deleted successfully!");
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

	public Batch convertToEntity(BatchRequest request) {
		Batch batch = new Batch();
		batch.setBatchName(request.getBatchName());
		batch.setBatchDescription(request.getBatchDescription());
		batch.setBatchCode(request.getBatchCode());

		batch.setCreatedAt(LocalDateTime.now());
		batch.setUpdatedAt(LocalDateTime.now());

		return batch;
	}

	public BatchResponse convertToResponse(Batch batch) {

		BatchResponse response = new BatchResponse();
		response.setBatchId(batch.getBatchId());
		response.setBatchName(batch.getBatchName());
		response.setBatchDescription(batch.getBatchDescription());
		response.setBatchCode(batch.getBatchCode());
		response.setCreatedBy(batch.getCreatedBy());
		response.setCreatedAt(batch.getCreatedAt());
		response.setUpdatedAt(batch.getUpdatedAt());

		return response;

	}

	@Override
	public ResponseEntity getAllBatches() {
		ResponseEntity response = new ResponseEntity();

		try {
			List<Batch> batches = batchRepository.findAll();

			if (batches.isEmpty()) {
				response.setMessage("No batches found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			List<BatchResponse> batchResponses = batches.stream().map(this::convertToResponse).toList();

			response.setMessage("Batches found successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(batchResponses);

		} catch (Exception e) {
			e.printStackTrace();

			response.setMessage("Internal server error!");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

}
