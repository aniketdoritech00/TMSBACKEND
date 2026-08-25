package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByProductCategoryId(Long productCategoryId);

    boolean existsByProductCode(String productCode);
}