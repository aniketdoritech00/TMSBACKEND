package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.DocumentSubProductRequest;
import com.doritech.tmsservice.request.DocumentSubProductUploadRequest;
import com.doritech.tmsservice.service.DocumentSubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/document-sub-products")
public class DocumentSubProductController {

	private static final Logger log = LoggerFactory.getLogger(DocumentSubProductController.class);

	private final DocumentSubProductService documentSubProductService;

	public DocumentSubProductController(DocumentSubProductService documentSubProductService) {
		this.documentSubProductService = documentSubProductService;
	}

	@PostMapping("/assignDocumentToSubProduct")
	public ResponseEntity assignDocumentToSubProduct(@Valid @RequestBody DocumentSubProductRequest request) {
		return documentSubProductService.assignDocumentToSubProduct(request);
	}

	@GetMapping("/getDocumentsBySubProductId/{subProductId}")
	public ResponseEntity getDocumentsBySubProductId(@PathVariable("subProductId") Long subProductId) {
		return documentSubProductService.getDocumentsBySubProductId(subProductId);
	}

	@DeleteMapping("/removeDocumentFromSubProduct")
	public ResponseEntity removeDocumentFromSubProduct(@RequestParam("documentId") Long documentId,
			@RequestParam("subProductId") Long subProductId) {
		return documentSubProductService.removeDocumentFromSubProduct(documentId, subProductId);
	}

	@PostMapping(value = "/uploadDocumentAndAssignToSubProducts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity uploadDocumentAndAssignToSubProducts(
			@Valid @RequestPart("documentData") DocumentSubProductUploadRequest request,
			@RequestPart("document") MultipartFile document) {
		return documentSubProductService.uploadDocumentAndAssignToSubProducts(request, document);
	}

	@PutMapping(value = "/updateDocumentAndAssignToSubProducts/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity updateDocumentAndAssignToSubProducts(@PathVariable Long documentId,
			@Valid @RequestPart("documentData") DocumentSubProductUploadRequest request,
			@RequestPart(value = "document", required = false) MultipartFile document) {
		return documentSubProductService.updateDocumentAndAssignToSubProducts(documentId, request, document);
	}
}