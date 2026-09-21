package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.tms.entity.TestAttempt;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

	long countByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);

	boolean existsByTestSet_TestSetIdAndUserIdAndAttemptNumber(Long testSetId, Long userId, Integer attemptNumber);

	List<TestAttempt> findByUserId(Long userId);

	Page<TestAttempt> findByUserId(Long userId, Pageable pageable);

	List<TestAttempt> findByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);

	Page<TestAttempt> findByTestSet_TestSetIdAndUserId(Long testSetId, Long userId, Pageable pageable);

	List<TestAttempt> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Page<TestAttempt> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId, Pageable pageable);

	List<TestAttempt> findByTrainingAssignment_TrainingAssignmentIdAndUserId(Long trainingAssignmentId, Long userId);

	Page<TestAttempt> findByTrainingAssignment_TrainingAssignmentIdAndUserId(Long trainingAssignmentId, Long userId,
			Pageable pageable);

	Optional<TestAttempt> findByUserIdAndStatus(Long userId, TestAttemptStatus status);

	Optional<TestAttempt> findTopByTestSet_TestSetIdOrderByTestAttemptIdDesc(Long testSetId);

}