package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.DirectTestAssignment;
import com.doritech.tmsservice.tms.entity.TestSet;

public interface DirectTestAssignmentRepository extends JpaRepository<DirectTestAssignment, Long> {

	boolean existsByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);

	List<DirectTestAssignment> findByUserId(Long currentUserId);

	List<DirectTestAssignment> findByBatch_BatchIdIn(List<Long> batchIds);

	boolean existsByTestSet_TestSetIdAndUserIdAndDirectTestAssignmentIdNot(Long testSetId, Long userId, Long id);

	Optional<TestSet> findByTestSet_TestSetIdAndUserId(Long testSetId, Long memberUserId);

	boolean existsByTestSet_TestSetIdAndBatch_BatchId(Long testSetId, Long batchId);

	boolean existsByTestSet_TestSetIdAndBatch_BatchIdAndDirectTestAssignmentIdNot(Long testSetId, Long batchId,
			Long id);

//	@Query("""
//			SELECT DISTINCT d
//			FROM DirectTestAssignment d
//			WHERE d.userId = :userId
//			   OR d.batchId IN (
//			       SELECT ub.batchId
//			       FROM UserBatch ub
//			       WHERE ub.userId = :userId
//			   )
//			""")
//	List<DirectTestAssignment> findAssignmentsForCurrentUser(@Param("userId") Long userId);
}
