package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.TestSet;

public interface TestSetRepository extends JpaRepository<TestSet, Long> {

	boolean existsByTestSetCode(String testCode);

	boolean existsBySetNo(String setNo);

	boolean existsByTestSetCodeAndTestSetIdNot(String testCode, Long testSetId);

	boolean existsBySetNoAndTestSetIdNot(String setNo, Long testSetId);

	List<TestSet> findByTraining_TrainingIdOrderByTestSetIdDesc(Long trainingId);

	List<TestSet> findByIsActiveTrueOrderByTestSetIdDesc();

	List<TestSet> findByTraining_TrainingIdAndIsActiveTrueOrderByTestSetIdDesc(Long trainingId);

	Page<TestSet> findByTraining_TrainingId(Long trainingId, Pageable pageable);
}