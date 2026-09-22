package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.VideoAssessment;

@Repository
public interface VideoAssessmentRepository extends JpaRepository<VideoAssessment, Long> {

	List<VideoAssessment> findByUserId(Long userId);

	List<VideoAssessment> findByTraining_TrainingId(Long trainingId);

	List<VideoAssessment> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Page<VideoAssessment> findByUserId(Long userId, Pageable pageable);

	Optional<VideoAssessment> findByVideoAssessmentIdAndUserId(Long videoAssessmentId, Long userId);

	boolean existsByTrainingAssignment_TrainingAssignmentIdAndUserId(Long trainingAssignmentId, Long userId);
}