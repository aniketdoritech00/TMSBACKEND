package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.TestQuestion;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

	List<TestQuestion> findByTestSet_TestSetIdOrderByDisplayOrderAsc(Long testSetId);

	boolean existsByTestSet_TestSetIdAndQuestionText(Long testSetId, String questionText);

	boolean existsByTestSet_TestSetIdAndQuestionTextAndTestQuestionIdNot(Long testSetId, String questionText,
			Long testQuestionId);

	Page<TestQuestion> findByTestSet_TestSetId(Long testSetId, Pageable pageable);
}