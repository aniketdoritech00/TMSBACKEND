package com.doritech.tmsservice.tms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

	boolean existsByProductCategoryCode(String productCategoryCode);

	boolean existsByProductCategoryName(String productCategoryName);

	boolean existsByProductCategoryDisplayOrder(Integer productCategoryDisplayOrder);

	boolean existsByProductCategoryCodeAndProductCategoryIdNot(String productCategoryCode, Long id);

	boolean existsByProductCategoryNameAndProductCategoryIdNot(String productCategoryName, Long id);

	boolean existsByProductCategoryDisplayOrderAndProductCategoryIdNot(Integer productCategoryDisplayOrder, Long id);

	@Query("""
			SELECT pc
			FROM ProductCategory pc
			WHERE (:productCategoryName IS NULL
			       OR LOWER(pc.productCategoryName) LIKE LOWER(CONCAT('%', :productCategoryName, '%')))
			AND (:productCategoryCode IS NULL
			       OR LOWER(pc.productCategoryCode) LIKE LOWER(CONCAT('%', :productCategoryCode, '%')))
			AND (:isActive IS NULL
			       OR pc.isActive = :isActive)
			""")
	Page<ProductCategory> findProductCategoryFilter(@Param("productCategoryName") String productCategoryName,
			@Param("productCategoryCode") String productCategoryCode, @Param("isActive") Boolean isActive,
			Pageable pageable);

}