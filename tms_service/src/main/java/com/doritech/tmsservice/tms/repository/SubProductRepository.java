package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.SubProduct;

@Repository
public interface SubProductRepository extends JpaRepository<SubProduct, Long> {

	List<SubProduct> findByProduct_ProductId(Long productId);

	boolean existsBySubProductCode(String subProductCode);

	boolean existsBySubProductNameAndProduct_ProductId(String trim, Long productId);

	boolean existsByDisplayOrderAndProduct_ProductId(Integer displayOrder, Long productId);

	boolean existsBySubProductNameAndProduct_ProductIdAndSubProductIdNot(String subProductName, Long productId,
			Long id);

	boolean existsByDisplayOrderAndProduct_ProductIdAndSubProductIdNot(Integer displayOrder, Long productId, Long id);

	boolean existsByProduct_ProductId(Long id);

	// org.springframework.data.domain.Page<SubProduct>
	// findSubProductByFilter(String productName, String subProductName,
	// org.springframework.data.domain.Pageable pageable);

	@Query("""
			SELECT sp
			FROM SubProduct sp
			JOIN sp.product p
			WHERE
			    (:productName IS NULL OR
			     LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')))
			AND
			    (:subProductName IS NULL OR
			     LOWER(sp.subProductName) LIKE LOWER(CONCAT('%', :subProductName, '%')))
			AND
			    (:isActive IS NULL OR sp.isActive = :isActive)
			""")
	Page<SubProduct> findSubProductByFilter(@Param("productName") String productName,
			@Param("subProductName") String subProductName, @Param("isActive") Boolean isActive, Pageable pageable);

	List<SubProduct> findByIsActiveTrue();
}
