package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

	boolean existsByTrainingCode(String trainingCode);

	List<Training> findByTrainingCategoryId(Long trainingCategoryId);
}