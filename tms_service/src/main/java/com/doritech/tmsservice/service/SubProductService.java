package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface SubProductService {

    ResponseEntity createSubProduct(List<SubProductRequest> subProductRequest);

    ResponseEntity getSubProductById(Long id);

    ResponseEntity getAllSubProduct(int page, int size, String sortBy, String sortDir);

    ResponseEntity getSubProductsByProductId(Long productId);

    ResponseEntity deleteSubProduct(Long id);
}