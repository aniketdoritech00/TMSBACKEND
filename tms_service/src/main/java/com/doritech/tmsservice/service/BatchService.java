package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.BatchRequest;

public interface BatchService {

	ResponseEntity createBatch(BatchRequest request);

	ResponseEntity updateBatch(Long id, BatchRequest request);

	ResponseEntity getAllBatch();

	ResponseEntity getBatchbyId(Long batchId);

	ResponseEntity deleteBatchbyId(Long batchId);

}
