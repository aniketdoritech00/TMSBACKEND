package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.DocumentRequest;
import com.doritech.tmsservice.service.DocumentService;

@RestController
@RequestMapping("/api/tms/documents")
public class DocumentController {

	private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@PostMapping(value = "/createDocument", consumes = "multipart/form-data")
	public ResponseEntity createDocument(@RequestParam("file") MultipartFile file,
			@RequestParam("documentName") String documentName,
			@RequestParam(value = "documentDescription", required = false) String documentDescription,
			@RequestParam(value = "documentType", required = false) String documentType,
			@RequestParam(value = "isSecure", required = false) Boolean isSecure,
			@RequestParam("uploadedBy") Long uploadedBy) {

		log.info("createDocument :: request received for name={}", documentName);

		DocumentRequest request = new DocumentRequest();
		request.setDocumentName(documentName);
		request.setDocumentDescription(documentDescription);
		request.setDocumentType(documentType);
		request.setIsSecure(isSecure);
		request.setUploadedBy(uploadedBy);

		return documentService.createDocument(request, file);
	}

	@GetMapping("/getDocumentById/{id}")
	public ResponseEntity getDocumentById(@PathVariable("id") Long id) {
		log.info("getDocumentById :: request received for id={}", id);
		return documentService.getDocumentById(id);
	}

	@GetMapping("/getAllDocument")
	public ResponseEntity getAllDocument(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "documentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllDocument :: request received with page={}, size={}", page, size);
		return documentService.getAllDocument(page, size, sortBy, sortDir);
	}

	@DeleteMapping("/deleteDocument/{id}")
	public ResponseEntity deleteDocument(@PathVariable("id") Long id) {
		log.info("deleteDocument :: request received for id={}", id);
		return documentService.deleteDocument(id);
	}
}