package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.ObserverAssignment;

public interface ObserverAssignmentRepository extends JpaRepository<ObserverAssignment, Long> {

	Optional<ObserverAssignment> findByUserIdAndTrainingAssignment_TrainingAssignmentId(Long userId,
			Long trainingAssignmentId);

	boolean existsByUserIdAndTrainingAssignment_TrainingAssignmentId(Long userId, Long trainingAssignmentId);

	boolean existsByUserIdAndTrainingAssignment_TrainingAssignmentIdAndObserverAssignmentIdNot(Long userId,
			Long trainingAssignmentId, Long observerAssignmentId);
}
