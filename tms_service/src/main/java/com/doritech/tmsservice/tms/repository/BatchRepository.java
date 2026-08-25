package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.Batch;

public interface BatchRepository extends JpaRepository<Batch, Long> {

	Optional<Batch> findByBatchName(String batchName);

	Optional<Batch> findByBatchCode(String batchCode);

	Optional<Batch> findByBatchNameAndBatchIdNot(String batchName, Long batchId);

	Optional<Batch> findByBatchCodeAndBatchIdNot(String batchCode, Long batchId);

}
