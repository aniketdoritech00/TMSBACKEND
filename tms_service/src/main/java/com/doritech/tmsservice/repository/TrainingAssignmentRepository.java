package com.doritech.tmsservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.entity.TrainingAssignment;

public interface TrainingAssignmentRepository extends JpaRepository<TrainingAssignment, Long> {

	boolean existsByTrainingIdAndUserId(Long trainingId, Long userId);

	Optional<TrainingAssignment> findByTrainingIdAndUserId(Long trainingId, Long userId);

	List<TrainingAssignment> findByUserId(Long userId);

	List<TrainingAssignment> findByTrainingId(Long trainingId);
}