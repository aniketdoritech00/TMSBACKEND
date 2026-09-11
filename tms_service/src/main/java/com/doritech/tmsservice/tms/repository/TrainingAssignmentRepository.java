package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.TrainingAssignment;

public interface TrainingAssignmentRepository extends JpaRepository<TrainingAssignment, Long> {

	List<TrainingAssignment> findByTraining_TrainingId(Long trainingId);

	Page<TrainingAssignment> findByTraining_TrainingId(Long trainingId, Pageable pageable);

	boolean existsByTraining_TrainingIdAndUserId(Long trainingId, Long userId);

	List<TrainingAssignment> findByUserId(Long userId);

	boolean existsByTraining_TrainingIdAndUserIdAndTrainingAssignmentIdNot(Long trainingId, Long userId, Long id);

	Page<TrainingAssignment> findByUserId(Long userId, Pageable pageable);

	@Query("""
			    SELECT ta
			    FROM TrainingAssignment ta
			    WHERE ta.userId = :userId
			       OR (
			            ta.userId IS NULL
			            AND ta.batch.batchId IN (
			                SELECT ub.batch.batchId
			                FROM UserBatch ub
			                WHERE ub.userId = :userId
			            )
			       )
			""")
	List<TrainingAssignment> findAssignmentsByUserIdOrBatch(@Param("userId") Long userId);

	@Query("""
			    SELECT ta
			    FROM TrainingAssignment ta
			    WHERE ta.userId = :userId
			       OR (
			            ta.userId IS NULL
			            AND ta.batch.batchId IN (
			                SELECT ub.batch.batchId
			                FROM UserBatch ub
			                WHERE ub.userId = :userId
			            )
			       )
			""")
	Page<TrainingAssignment> findAssignmentsByUserIdOrBatch(@Param("userId") Long userId, Pageable pageable);

}
