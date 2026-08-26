package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByProductName(String productName);

	boolean existsByProductCode(String productCode);

	boolean existsByDisplayOrder(Integer displayOrder);

	boolean existsByProductNameAndProductIdNot(String productName, Long productId);

	boolean existsByProductCodeAndProductIdNot(String productCode, Long productId);

	boolean existsByDisplayOrderAndProductIdNot(Integer displayOrder, Long productId);

	List<Product> findByProductCategory_ProductCategoryId(Long categoryId);
}
