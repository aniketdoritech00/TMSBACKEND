package com.doritech.tmsservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.DocumentRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface DocumentService {

	ResponseEntity createDocument(DocumentRequest documentRequest, MultipartFile file);

	ResponseEntity getDocumentDetailsById(Long id);

	ResponseEntity getAllDocument(int page, int size, String sortBy, String sortDir);

	ResponseEntity deleteDocument(Long id);
}