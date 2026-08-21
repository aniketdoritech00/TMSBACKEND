package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.Batch;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.repository.BatchRepository;
import com.doritech.tmsservice.request.BatchRequest;
import com.doritech.tmsservice.response.BatchResponse;
import com.doritech.tmsservice.service.BatchService;

@Service
public class BatchServiceImpl implements BatchService {

	private final BatchRepository batchRepository;

	public BatchServiceImpl(BatchRepository batchRepository) {
		this.batchRepository = batchRepository;

	}

	@Override
	public ResponseEntity createBatch(BatchRequest request) {

		ResponseEntity response = new ResponseEntity();

		if (request == null) {

			response.setMessage("Request is  null");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		}
		try {

			// Check duplicate batch name
			if (batchRepository.findByBatchName(request.getBatchName()).isPresent()) {

				response.setMessage("Batch name already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);

				return response;
			}
			// Check duplicate batch code
			if (request.getBatchCode() != null && batchRepository.findByBatchCode(request.getBatchCode()).isPresent()) {

				response.setMessage("Batch code already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);

				return response;
			}

			Batch batch = convertToEntity(request);

			Batch savedBatch = batchRepository.save(batch);

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
	/*
	 * try { Batch batch = convertToEntity(request); Batch saved =
	 * batchRepository.save(batch);
	 * 
	 * response.setMessage("Batch created successfully!");
	 * response.setStatusCode(HttpStatus.CREATED.value());
	 * response.setPayload(saved);
	 * 
	 * } catch (Exception e) { e.getStackTrace();
	 * response.setMessage("Internal server error!");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); } return response; }
	 */

	@Override
	public ResponseEntity updateBatch(Long batchId, BatchRequest request) {
		ResponseEntity response = new ResponseEntity();
		if (request == null) {

			response.setMessage("Request is  null");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		}

		try {

			// 1. Check whether batch exists
			Optional<Batch> optionalBatch = batchRepository.findById(batchId);

			if (optionalBatch.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			// 2. Check duplicate batch name
			if (batchRepository.findByBatchNameAndBatchIdNot(request.getBatchName(), batchId).isPresent()) {

				response.setMessage("Batch name already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);
				return response;
			}

			// 3. Check duplicate batch code
			if (request.getBatchCode() != null
					&& batchRepository.findByBatchCodeAndBatchIdNot(request.getBatchCode(), batchId).isPresent()) {

				response.setMessage("Batch code already exists!");
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setPayload(null);
				return response;
			}

			// 4. Get existing batch
			Batch batch = optionalBatch.get();

			// 5. Update fields
			batch.setBatchName(request.getBatchName());
			batch.setBatchDescription(request.getBatchDescription());
			batch.setBatchCode(request.getBatchCode());
			batch.setCreatedBy(request.getCreatedBy());

			// 6. Save
			Batch updatedBatch = batchRepository.save(batch);

			// 7. Convert to response
			BatchResponse batchResponse = convertToResponse(updatedBatch);

			response.setMessage("Batch updated successfully!");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(batchResponse);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			response.setMessage("Batch name or batch code already exists!");
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setPayload(null);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(null);
		}

		return response;
	}

	/*
	 * try {
	 * 
	 * Optional<Batch> optBatch = batchRepository.findById(batchId);
	 * 
	 * if (optBatch.get() == null) { response.setMessage("Batch not found!");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(null); return response; } Batch batch = optBatch.get();
	 * 
	 * batch.setBatchName(request.getBatchName());
	 * batch.setBatchDescription(request.getBatchDescription());
	 * batch.setBatchCode(request.getBatchCode());
	 * batch.setCreatedBy(request.getCreatedBy());
	 * 
	 * Batch updatedBatch = batchRepository.save(batch);
	 * 
	 * BatchResponse batchResponse = convertToResponse(updatedBatch);
	 * 
	 * response.setMessage("Batch updated successfully!");
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setPayload(batchResponse);
	 * 
	 * } catch (Exception e) { e.getStackTrace();
	 * response.setMessage("Internal server error!");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); } return response; }
	 */

	@Override
	public ResponseEntity getAllBatch() {
		ResponseEntity response = new ResponseEntity();

		try {

			List<Batch> batches = batchRepository.findAll();

			if (batches == null || batches.isEmpty()) {

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
	
	
	/*
	 * try { List<Batch> batches = batchRepository.findAll(); if (batches == null) {
	 * response.setMessage("Batch not found!");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(null); return response; }
	 * response.setMessage("Batch found successfully!");
	 * response.setStatusCode(HttpStatus.OK.value()); response.setPayload(batches);
	 * return response; } catch (Exception e) { e.getStackTrace();
	 * response.setMessage("Internal server error!");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); return response; } }
	 */

	@Override
	public ResponseEntity getBatchbyId(Long batchId) {
		ResponseEntity response = new ResponseEntity();
		try {

			// Validate batchId
			if (batchId == null) {
				response.setMessage("Batch ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			// Find batch
			Optional<Batch> optionalBatch = batchRepository.findById(batchId);

			// Batch not found
			if (optionalBatch.isEmpty()) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			// Convert entity to response DTO
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

	/*
	 * if (batch.get() == null) { response.setMessage("Batch not found!");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(null); return response; }
	 * response.setMessage("Batch found successfully!");
	 * response.setStatusCode(HttpStatus.OK.value());
	 * response.setPayload(batch.get()); return response; } catch (Exception e) {
	 * e.getStackTrace(); response.setMessage("Internal server error!");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); return response; }
	 * 
	 * }
	 */
	@Override
	public ResponseEntity deleteBatchbyId(Long batchId) {
		ResponseEntity response = new ResponseEntity();
		try {

			// Validate batchId
			if (batchId == null) {
				response.setMessage("Batch ID is required!");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(null);
				return response;
			}

			// Check whether batch exists
			if (!batchRepository.existsById(batchId)) {
				response.setMessage("Batch not found!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(null);
				return response;
			}

			// Delete batch
			batchRepository.deleteById(batchId);

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

	
	/*
	 * Boolean isPresent = batchRepository.existsById(batchId);
	 * 
	 * if (!isPresent) { response.setMessage("Batch not found!");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(null); return response; }
	 * batchRepository.deleteById(batchId);
	 * response.setMessage("Batch deleted successfully!");
	 * response.setStatusCode(HttpStatus.OK.value()); response.setPayload(null); }
	 * catch (Exception e) { e.getStackTrace();
	 * response.setMessage("Internal server error!");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); }
	 * 
	 * return response; }
	 */

	public Batch convertToEntity(BatchRequest request) {
		Batch batch = new Batch();
		batch.setBatchName(request.getBatchName());
		batch.setBatchDescription(request.getBatchDescription());
		batch.setBatchCode(request.getBatchCode());
		batch.setCreatedBy(request.getCreatedBy());
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

}
