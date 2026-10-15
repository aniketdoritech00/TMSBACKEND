package com.doritech.tmsservice.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.DocumentRequest;
import com.doritech.tmsservice.service.DocumentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/documents")
public class DocumentController {

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@PostMapping(value = "/createDocument", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity createDocument(@RequestParam("file") MultipartFile file,
			@RequestParam("documentName") String documentName,
			@RequestParam(value = "documentDescription", required = false) String documentDescription,
			@RequestParam(value = "documentType", required = false) String documentType,
			@RequestParam(value = "isSecure", required = false) Boolean isSecure) {

		DocumentRequest request = new DocumentRequest();

		request.setDocumentName(documentName);
		request.setDocumentDescription(documentDescription);
		request.setIsSecure(isSecure);

		return documentService.createDocument(request, file);
	}

	@GetMapping("/getDocumentDetailsById/{id}")
	public ResponseEntity getDocumentDetailsById(@PathVariable("id") Long id) {
		return documentService.getDocumentDetailsById(id);
	}

	@GetMapping("/getAllDocument")
	public ResponseEntity getAllDocument(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "documentId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return documentService.getAllDocument(page, size, sortBy, sortDir);
	}

	@GetMapping("/previewDocument/{id}")
	public org.springframework.http.ResponseEntity<Resource> previewDocument(@PathVariable("id") Long id) {
	    return documentService.previewDocument(id);
	}
	
	@DeleteMapping("/deleteDocument/{id}")
	public ResponseEntity deleteDocument(@PathVariable("id") Long id) {
		return documentService.deleteDocument(id);
	}
	
	@GetMapping("/downloadDocument/{id}")
	public org.springframework.http.ResponseEntity<Resource> downloadDocument(
	        @PathVariable("id") Long id) {
	    return documentService.downloadDocument(id);
	}
}