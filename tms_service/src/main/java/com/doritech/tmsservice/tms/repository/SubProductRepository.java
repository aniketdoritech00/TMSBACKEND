package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}