package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.DocumentSubProduct;
import com.doritech.tmsservice.tms.entity.DocumentSubProduct.DocumentSubProductId;

@Repository
public interface DocumentSubProductRepository extends JpaRepository<DocumentSubProduct, DocumentSubProductId> {

	List<DocumentSubProduct> findByIdSubProductId(Long subProductId);

	List<DocumentSubProduct> findByIdDocumentId(Long documentId);
}