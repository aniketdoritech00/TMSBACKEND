package com.doritech.tmsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.TestQuestion;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

	List<TestQuestion> findByTestSetIdOrderByDisplayOrderAsc(Long testSetId);

	long countByTestSetId(Long testSetId);
}