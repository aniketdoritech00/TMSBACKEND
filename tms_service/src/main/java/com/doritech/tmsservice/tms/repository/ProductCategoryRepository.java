package com.doritech.tmsservice.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

}