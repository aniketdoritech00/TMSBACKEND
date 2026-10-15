package com.doritech.tmsservice.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.DirectTestAssignment;

public interface DirectTestAssignmentRepository extends JpaRepository<DirectTestAssignment, Long> {

	boolean existsByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);
}
