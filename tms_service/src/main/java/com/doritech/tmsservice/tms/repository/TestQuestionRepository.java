package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.TestQuestion;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

	List<TestQuestion> findByTestSetIdOrderByDisplayOrderAsc(Long testSetId);

	long countByTestSetId(Long testSetId);
}