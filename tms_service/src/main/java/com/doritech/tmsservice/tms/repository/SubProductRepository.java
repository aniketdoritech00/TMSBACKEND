package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.SubProduct;
@Repository
public interface SubProductRepository extends JpaRepository<SubProduct, Long> {

	List<SubProduct> findByProductId(Long productId);

	boolean existsBySubProductCode(String subProductCode);
}