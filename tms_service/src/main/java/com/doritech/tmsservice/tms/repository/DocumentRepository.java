package com.doritech.tmsservice.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Document;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}