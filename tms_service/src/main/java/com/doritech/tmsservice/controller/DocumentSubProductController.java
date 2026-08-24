package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.DocumentSubProductRequest;
import com.doritech.tmsservice.service.DocumentSubProductService;

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
		log.info("assignDocumentToSubProduct :: request received for documentId={}, subProductId={}",
				request.getDocumentId(), request.getSubProductId());
		return documentSubProductService.assignDocumentToSubProduct(request);
	}

	@GetMapping("/getDocumentsBySubProductId/{subProductId}")
	public ResponseEntity getDocumentsBySubProductId(@PathVariable("subProductId") Long subProductId) {
		log.info("getDocumentsBySubProductId :: request received for subProductId={}", subProductId);
		return documentSubProductService.getDocumentsBySubProductId(subProductId);
	}

	@DeleteMapping("/removeDocumentFromSubProduct")
	public ResponseEntity removeDocumentFromSubProduct(@RequestParam("documentId") Long documentId,
			@RequestParam("subProductId") Long subProductId) {
		log.info("removeDocumentFromSubProduct :: request received for documentId={}, subProductId={}", documentId,
				subProductId);
		return documentSubProductService.removeDocumentFromSubProduct(documentId, subProductId);
	}
}