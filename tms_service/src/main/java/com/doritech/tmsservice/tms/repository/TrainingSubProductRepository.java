package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.TrainingSubProduct;
import com.doritech.tmsservice.tms.entity.TrainingSubProductId;

public interface TrainingSubProductRepository extends JpaRepository<TrainingSubProduct, TrainingSubProductId> {

	boolean existsByTraining_TrainingIdAndSubProduct_SubProductId(Long trainingId, Long subProductId);

	Optional<TrainingSubProduct> findByTraining_TrainingIdAndSubProduct_SubProductId(Long trainingId,
			Long subProductId);

	List<TrainingSubProduct> findByTraining_TrainingId(Long trainingId);

	List<TrainingSubProduct> findBySubProduct_SubProductId(Long subProductId);

	@Query("""
			    SELECT tsp
			    FROM TrainingSubProduct tsp
			    JOIN FETCH tsp.training
			    JOIN FETCH tsp.subProduct
			    WHERE tsp.subProduct.subProductId = :subProductId
			""")
	List<TrainingSubProduct> findBySubProductIdWithDetails(@Param("subProductId") Long subProductId);
}