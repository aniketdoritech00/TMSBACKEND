package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.DocumentSubProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface DocumentSubProductService {

    ResponseEntity assignDocumentToSubProduct(DocumentSubProductRequest documentSubProductRequest);

    ResponseEntity getDocumentsBySubProductId(Long subProductId);

    ResponseEntity removeDocumentFromSubProduct(Long documentId, Long subProductId);
}