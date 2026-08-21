package com.doritech.tmsservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.DocumentRequest;

public interface DocumentService {

	ResponseEntity createDocument(DocumentRequest documentRequest, MultipartFile file);

	ResponseEntity getDocumentById(Long id);

	ResponseEntity getAllDocument(int page, int size, String sortBy, String sortDir);

	ResponseEntity deleteDocument(Long id);
}