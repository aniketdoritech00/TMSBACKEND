package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.DocumentSubProductRequest;

public interface DocumentSubProductService {

    ResponseEntity assignDocumentToSubProduct(DocumentSubProductRequest documentSubProductRequest);

    ResponseEntity getDocumentsBySubProductId(Long subProductId);

    ResponseEntity removeDocumentFromSubProduct(Long documentId, Long subProductId);
}