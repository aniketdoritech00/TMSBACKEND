package com.doritech.tmsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.Document;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}