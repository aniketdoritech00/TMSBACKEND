package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.InTrainingUserResponse;

@Repository
public interface InTrainingUserResponseRepository extends JpaRepository<InTrainingUserResponse, Long> {

	List<InTrainingUserResponse> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	List<InTrainingUserResponse> findByQuestion_QuestionId(Long questionId);

	boolean existsByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(Long questionId,
			Long trainingAssignmentId);

	long countByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	long countByTrainingAssignment_TrainingAssignmentIdAndIsCorrect(Long trainingAssignmentId, Boolean isCorrect);

	long countByTrainingAssignment_TrainingAssignmentIdAndIsSkipped(Long trainingAssignmentId, Boolean isSkipped);

	void deleteByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Optional<InTrainingUserResponse> findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(
			Long questionId, Long trainingAssignmentId);
}