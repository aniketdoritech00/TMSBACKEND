package com.doritech.tmsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.DocumentSubProduct;
import com.doritech.tmsservice.entity.DocumentSubProduct.DocumentSubProductId;

@Repository
public interface DocumentSubProductRepository extends JpaRepository<DocumentSubProduct, DocumentSubProductId> {

	List<DocumentSubProduct> findByIdSubProductId(Long subProductId);

	List<DocumentSubProduct> findByIdDocumentId(Long documentId);
}