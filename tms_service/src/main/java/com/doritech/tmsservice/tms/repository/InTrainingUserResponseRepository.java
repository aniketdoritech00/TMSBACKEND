package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.InTrainingUserResponse;

@Repository
public interface InTrainingUserResponseRepository extends JpaRepository<InTrainingUserResponse, Long> {

	Optional<InTrainingUserResponse> findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(
			Long questionId, Long trainingAssignmentId);

	Page<InTrainingUserResponse> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId,
			Pageable pageable);

	Page<InTrainingUserResponse> findByQuestion_QuestionId(Long questionId, Pageable pageable);

	long countByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	long countByTrainingAssignment_TrainingAssignmentIdAndIsCorrect(Long trainingAssignmentId, Boolean isCorrect);

	long countByTrainingAssignment_TrainingAssignmentIdAndUserAnswerIsNotNullAndUserAnswerNot(Long trainingAssignmentId,
			String userAnswer);

	void deleteByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	List<InTrainingUserResponse> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);
}