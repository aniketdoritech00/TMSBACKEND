package com.doritech.tmsservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.DocumentSubProductRequest;
import com.doritech.tmsservice.request.DocumentSubProductUploadRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

public interface DocumentSubProductService {

    ResponseEntity assignDocumentToSubProduct(DocumentSubProductRequest documentSubProductRequest);

    ResponseEntity getDocumentsBySubProductId(Long subProductId);

    ResponseEntity removeDocumentFromSubProduct(Long documentId, Long subProductId);

	ResponseEntity uploadDocumentAndAssignToSubProducts(@Valid DocumentSubProductUploadRequest request,
			MultipartFile document);

	ResponseEntity updateDocumentAndAssignToSubProducts(Long documentId, @Valid DocumentSubProductUploadRequest request,
			MultipartFile document);
}