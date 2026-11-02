
package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Certificate;

@Repository
public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByTestAttemptId(Long testAttemptId);

    Optional<Certificate> findByCertificateNumber(String certificateNumber);
}
