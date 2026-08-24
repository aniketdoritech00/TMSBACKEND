package com.doritech.tmsservice.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.QuestionOption;
@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

	List<QuestionOption> findByTestQuestionIdOrderByDisplayOrderAsc(Long testQuestionId);

	long countByTestQuestionIdAndIsCorrectTrue(Long testQuestionId);

	void deleteByTestQuestionId(Long testQuestionId);
}