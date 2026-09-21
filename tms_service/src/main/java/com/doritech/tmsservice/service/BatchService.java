package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.BatchRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface BatchService {

	ResponseEntity createBatch(BatchRequest request);

	ResponseEntity updateBatch(Long id, BatchRequest request);

	ResponseEntity getAllBatch(int page, int size);

	ResponseEntity getBatchbyId(Long batchId);

	ResponseEntity deleteBatchbyId(Long batchId);

	ResponseEntity getAllBatches();

	ResponseEntity getAllBatch(int page, int size, String branchCode, String branchName);

}
