package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.enums.TrainingStatus;
import com.doritech.tmsservice.tms.entity.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

	boolean existsByTrainingCode(String trainingCode);

	List<Training> findByTrainingCategoryId(Long trainingCategoryId);

	boolean existsByTrainingCodeAndTrainingIdNot(String trainingCode, Long id);

	@Query("""
			SELECT t
			FROM Training t
			WHERE
			    (:trainingCategoryId IS NULL
			     OR t.trainingCategoryId = :trainingCategoryId)
			AND
			    (:trainingName IS NULL
			     OR LOWER(t.trainingName) LIKE LOWER(CONCAT('%', :trainingName, '%')))
			AND
			    (:status IS NULL
			     OR t.status = :status)
			""")
	Page<Training> findTrainingByFilter(@Param("trainingCategoryId") Long trainingCategoryId,
			@Param("trainingName") String trainingName, @Param("status") TrainingStatus status, Pageable pageable);
}