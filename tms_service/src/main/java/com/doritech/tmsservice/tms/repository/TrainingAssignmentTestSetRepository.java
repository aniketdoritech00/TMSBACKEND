package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.TrainingAssignmentTestSet;

public interface TrainingAssignmentTestSetRepository extends JpaRepository<TrainingAssignmentTestSet, Long> {

	boolean existsByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Optional<TrainingAssignmentTestSet> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	@Query("""
			    SELECT COUNT(t) > 0
			    FROM TrainingAssignmentTestSet t
			    JOIN t.trainingAssignment ta
			    WHERE t.testSet.testSetId = :testSetId
			    AND ta.userId = :userId
			""")
	boolean existsIndividualTrainingTestAssignment(@Param("testSetId") Long testSetId, @Param("userId") Long userId);

	@Query("""
			    SELECT COUNT(t) > 0
			    FROM TrainingAssignmentTestSet t
			    JOIN t.trainingAssignment ta
			    JOIN ta.batch b
			    JOIN UserBatch ub ON ub.batch.batchId = b.batchId
			    WHERE t.testSet.testSetId = :testSetId
			    AND ub.userId = :userId
			""")
	boolean existsBatchTrainingTestAssignment(@Param("testSetId") Long testSetId, @Param("userId") Long userId);
}