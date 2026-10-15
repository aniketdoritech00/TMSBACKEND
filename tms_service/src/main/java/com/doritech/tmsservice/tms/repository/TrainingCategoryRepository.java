package com.doritech.tmsservice.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.TrainingCategory;
@Repository
public interface TrainingCategoryRepository extends JpaRepository<TrainingCategory, Long> {

	boolean existsByCategoryCode(String categoryCode);

	boolean existsByCategoryName(String categoryName);

	boolean existsByCategoryCodeAndTrainingCategoryIdNot(String categoryCode, Long id);

	boolean existsByCategoryNameAndTrainingCategoryIdNot(String categoryName, Long id);
}