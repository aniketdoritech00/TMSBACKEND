package com.doritech.tmsservice.tms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.VideoAssessmentQuestion;

@Repository
public interface VideoAssessmentQuestionRepository extends JpaRepository<VideoAssessmentQuestion, Long> {

	Page<VideoAssessmentQuestion> findByTraining_TrainingId(Long trainingId, Pageable pageable);

	boolean existsByTraining_TrainingIdAndQuestionTextIgnoreCase(Long trainingId, String questionText);

	boolean existsByTraining_TrainingIdAndQuestionTextIgnoreCaseAndVideoAssessmentQuestionIdNot(Long trainingId,
			String questionText, Long videoAssessmentQuestionId);
}