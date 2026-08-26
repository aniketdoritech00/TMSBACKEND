package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

public interface SubProductService {

	ResponseEntity getSubProductById(Long id);

	ResponseEntity getAllSubProduct(int page, int size, String sortBy, String sortDir);

	ResponseEntity getSubProductsByProductId(Long productId);

	ResponseEntity deleteSubProduct(Long id);

	ResponseEntity createSubProduct(SubProductRequest request);

	ResponseEntity updateSubProduct(Long id, @Valid SubProductRequest request);
}