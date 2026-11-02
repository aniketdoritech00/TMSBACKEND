
package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.enums.TestResult;
import com.doritech.tmsservice.service.CertificateService;
import com.doritech.tmsservice.service.DirectTestCertificateService;
import com.doritech.tmsservice.tms.entity.Certificate;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.repository.CertificateRepository;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.erp.repository.UserMasterRepository;

@Service
public class DirectTestCertificateServiceImpl
        implements DirectTestCertificateService {

    private final CertificateService certificateService;
    private final TestAttemptRepository testAttemptRepository;
    private final UserMasterRepository userMasterRepository;
    private final CertificateRepository certificateRepository;

    public DirectTestCertificateServiceImpl(
            CertificateService certificateService,
            TestAttemptRepository testAttemptRepository,
            UserMasterRepository userMasterRepository,
            CertificateRepository certificateRepository) {

        this.certificateService = certificateService;
        this.testAttemptRepository = testAttemptRepository;
        this.userMasterRepository = userMasterRepository;
        this.certificateRepository = certificateRepository;
    }

    @Override
    @Transactional("tmsTransactionManager")
    public byte[] generateDirectTestCertificate(Long testAttemptId) {

        // 1. Validate attempt ID
        if (testAttemptId == null || testAttemptId <= 0) {
            throw new IllegalArgumentException("Invalid test attempt ID");
        }

        // 2. Fetch test attempt
        TestAttempt attempt = testAttemptRepository.findById(testAttemptId)
                .orElseThrow(() ->
                        new RuntimeException("Test attempt not found: " + testAttemptId));

        // 3. Validate completion
        if (attempt.getStatus() != TestAttemptStatus.COMPLETED) {
            throw new RuntimeException(
                    "Certificate cannot be generated. Test is not completed.");
        }

        // 4. Validate PASS result
        if (attempt.getResult() != TestResult.PASS) {
            throw new RuntimeException(
                    "Certificate is available only for passed tests.");
        }

        // 5. Check existing certificate
        // If already generated, return the saved PDF.
        var existingCertificate =
                certificateRepository.findByTestAttemptId(testAttemptId);

        if (existingCertificate.isPresent()) {

            byte[] existingPdf =
                    existingCertificate.get().getCertificatePdf();

            if (existingPdf == null || existingPdf.length == 0) {
                throw new RuntimeException(
                        "Saved certificate PDF is empty.");
            }

            System.out.println("Existing certificate found: "
                    + existingCertificate.get().getCertificateNumber());

            return existingPdf;
        }

        // 6. Validate test set
        if (attempt.getTestSet() == null) {
            throw new RuntimeException("Test set not found.");
        }

        // 7. Get user ID
        Long userId = attempt.getUserId();

        if (userId == null || userId <= 0) {
            throw new RuntimeException("User ID not found.");
        }

        // 8. Fetch employee name
        String userName =
                userMasterRepository.findEmployeeNameByUserId(
                        Math.toIntExact(userId));

        if (userName == null || userName.trim().isEmpty()) {
            throw new RuntimeException(
                    "Employee name not found for user ID: " + userId);
        }

        // 9. Fetch test name
        String testName = attempt.getTestSet().getTestName();

        if (testName == null || testName.trim().isEmpty()) {
            throw new RuntimeException("Test name not found.");
        }

        Long testSetId = attempt.getTestSet().getTestSetId();

        // 10. Generate certificate number
        String certificateNumber = "TMS-CERT-" +
                UUID.randomUUID().toString()
                        .substring(0, 8)
                        .toUpperCase();

        // 11. Generate issue date
        LocalDate issueDate = LocalDate.now();

        String formattedIssueDate = issueDate.format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // 12. Generate PDF
        byte[] pdf = certificateService.generateCertificate(
                userName.trim(),
                testName.trim(),
                certificateNumber,
                formattedIssueDate
        );

        if (pdf == null || pdf.length == 0) {
            throw new RuntimeException(
                    "Certificate PDF generation failed.");
        }

        // 13. Save certificate details and PDF in database
        Certificate certificate = new Certificate();

        certificate.setCertificateNumber(certificateNumber);
        certificate.setTestAttemptId(testAttemptId);
        certificate.setUserId(userId);
        certificate.setTestSetId(testSetId);
        certificate.setUserName(userName.trim());
        certificate.setTestName(testName.trim());
        certificate.setIssueDate(issueDate);
        certificate.setCertificatePdf(pdf);

        Certificate savedCertificate =
                certificateRepository.saveAndFlush(certificate);

        // 14. Debug logs
        System.out.println("========== CERTIFICATE SAVED ==========");
        System.out.println("Certificate ID: "
                + savedCertificate.getCertificateId());
        System.out.println("Certificate Number: "
                + savedCertificate.getCertificateNumber());
        System.out.println("Test Attempt ID: " + testAttemptId);
        System.out.println("User ID: " + userId);
        System.out.println("Employee Name: " + userName);
        System.out.println("Test Name: " + testName);
        System.out.println("PDF Size: " + pdf.length + " bytes");
        System.out.println("========================================");

        // 15. Return PDF
        return pdf;
    }
}
