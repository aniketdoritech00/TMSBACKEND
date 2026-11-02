package com.doritech.tmsservice.service;

public interface CertificateService {

	byte[] generateCertificate(String userName, String testName, String certificateNumber, String issueDate);
}