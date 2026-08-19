package com.doritech.tmsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByProductCategoryId(Long productCategoryId);

    boolean existsByProductCode(String productCode);
}