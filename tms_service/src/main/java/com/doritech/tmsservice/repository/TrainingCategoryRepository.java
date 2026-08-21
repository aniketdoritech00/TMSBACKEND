package com.doritech.tmsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.TrainingCategory;
@Repository
public interface TrainingCategoryRepository extends JpaRepository<TrainingCategory, Long> {

	boolean existsByCategoryCode(String categoryCode);
}