package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByProductName(String productName);

	boolean existsByProductCode(String productCode);

	boolean existsByDisplayOrder(Integer displayOrder);

	boolean existsByProductNameAndProductIdNot(String productName, Long productId);

	boolean existsByProductCodeAndProductIdNot(String productCode, Long productId);

	boolean existsByDisplayOrderAndProductIdNot(Integer displayOrder, Long productId);

	List<Product> findByProductCategory_ProductCategoryId(Long categoryId);

	boolean existsByProductCategory_ProductCategoryId(Long id);

	@Query("""
			SELECT p
			FROM Product p
			JOIN FETCH p.productCategory pc
			WHERE (:productName IS NULL
			       OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')))

			AND (:productCode IS NULL
			     OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :productCode, '%')))

			AND (:productCategoryId IS NULL
			     OR pc.productCategoryId = :productCategoryId)

			AND (:isActive IS NULL
			     OR p.isActive = :isActive)
			""")
	Page<Product> findProductFilter(@Param("productName") String productName, @Param("productCode") String productCode,
			@Param("productCategoryId") Long productCategoryId, @Param("isActive") Boolean isActive, Pageable pageable);
}
