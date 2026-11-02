package com.doritech.tmsservice.service;

public interface DirectTestCertificateService {

	byte[] generateDirectTestCertificate(Long testAttemptId);
}