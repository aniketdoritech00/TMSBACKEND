package com.doritech.tmsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.entity.TestSet;

public interface TestSetRepository extends JpaRepository<TestSet, Long> {

	boolean existsByTestCode(String testCode);

	boolean existsBySetNo(String setNo);

	List<TestSet> findByTrainingId(Long trainingId);
}