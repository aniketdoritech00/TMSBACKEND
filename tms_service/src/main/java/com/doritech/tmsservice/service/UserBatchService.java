package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.UserBatchRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface UserBatchService {

	ResponseEntity assignBatchToUser(UserBatchRequest request);

	ResponseEntity updateUserBatch(Long userBatchId, UserBatchRequest request);

	ResponseEntity getUserBatchById(Long userBatchId);

	ResponseEntity deleteUserBatchById(Long userBatchId);

	ResponseEntity getAllUserBatches(int page, int size);

}