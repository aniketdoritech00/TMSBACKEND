package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.VideoAssessmentQuestion;

@Repository
public interface VideoAssessmentQuestionRepository extends JpaRepository<VideoAssessmentQuestion, Long> {

	Page<VideoAssessmentQuestion> findByTraining_TrainingId(Long trainingId, Pageable pageable);

	boolean existsByTraining_TrainingIdAndQuestionTextIgnoreCase(Long trainingId, String questionText);

	boolean existsByTraining_TrainingIdAndQuestionTextIgnoreCaseAndVideoAssessmentQuestionIdNot(Long trainingId,
			String questionText, Long videoAssessmentQuestionId);

	@Query("SELECT q FROM VideoAssessmentQuestion q " + "WHERE q.training.trainingId = :trainingId "
			+ "ORDER BY q.videoAssessmentQuestionId DESC")
	List<VideoAssessmentQuestion> findByTrainingId(@Param("trainingId") Long trainingId);
}