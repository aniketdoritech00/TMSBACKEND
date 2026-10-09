package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.TrainingAssignmentTestSet;

public interface TrainingAssignmentTestSetRepository extends JpaRepository<TrainingAssignmentTestSet, Long> {

	boolean existsByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Optional<TrainingAssignmentTestSet> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);
}