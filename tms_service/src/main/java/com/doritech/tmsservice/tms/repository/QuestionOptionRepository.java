package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.QuestionOption;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

	List<QuestionOption> findByTestQuestion_TestQuestionIdOrderByDisplayOrderAsc(Long testQuestionId);

	boolean existsByTestQuestion_TestQuestionIdAndOptionText(Long testQuestionId, String optionText);

	boolean existsByTestQuestion_TestQuestionIdAndOptionTextAndQuestionOptionIdNot(Long testQuestionId,
			String optionText, Long questionOptionId);

	Page<QuestionOption> findByTestQuestion_TestQuestionId(Long testQuestionId, Pageable pageable);
}