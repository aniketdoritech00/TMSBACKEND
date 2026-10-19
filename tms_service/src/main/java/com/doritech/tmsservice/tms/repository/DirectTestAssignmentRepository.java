package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.DirectTestAssignment;

public interface DirectTestAssignmentRepository extends JpaRepository<DirectTestAssignment, Long> {

	boolean existsByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);

	List<DirectTestAssignment> findByUserId(Long currentUserId);
	
	List<DirectTestAssignment> findByBatch_BatchIdIn(List<Long> batchIds);
}
