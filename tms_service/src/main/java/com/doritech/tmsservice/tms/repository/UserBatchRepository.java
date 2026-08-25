package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.UserBatch;

@Repository
public interface UserBatchRepository extends JpaRepository<UserBatch, Long> {

	Optional<UserBatch> findByUserIdAndBatch_BatchId(Long userId, Long batchId);

	boolean existsByUserIdAndBatch_BatchId(Long userId, Long batchId);

}