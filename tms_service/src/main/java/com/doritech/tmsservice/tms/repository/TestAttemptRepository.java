package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.tms.entity.TestAttempt;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

	List<TestAttempt> findByUserId(Long userId);

	List<TestAttempt> findByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);

	Optional<TestAttempt> findByTestSet_TestSetIdAndUserIdAndAttemptNumber(Long testSetId, Long userId,
			Integer attemptNumber);

	List<TestAttempt> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	List<TestAttempt> findByTrainingAssignment_TrainingAssignmentIdAndUserId(Long trainingAssignmentId, Long userId);

	long countByTestSet_TestSetIdAndUserId(Long testSetId, Long userId);

	boolean existsByTestSet_TestSetIdAndUserIdAndAttemptNumber(Long testSetId, Long userId, Integer attemptNumber);

	Optional<TestAttempt> findByUserIdAndStatus(Long userId, TestAttemptStatus inProgress);
}