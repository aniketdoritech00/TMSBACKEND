package com.doritech.tmsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.service.DirectTestCertificateService;

@RestController
@RequestMapping("/api/tms/certificates")
public class CertificateController {

	@Autowired
	private DirectTestCertificateService certificateService;

	@PostMapping("/generate/{testAttemptId}")
	public ResponseEntity<byte[]> generateCertificate(@PathVariable Long testAttemptId) {

		byte[] pdf = certificateService.generateDirectTestCertificate(testAttemptId);

		if (pdf == null || pdf.length == 0) {
			throw new RuntimeException("Certificate PDF generation failed. PDF is empty.");
		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf")
				.contentType(MediaType.APPLICATION_PDF).contentLength(pdf.length).body(pdf);
	}
}