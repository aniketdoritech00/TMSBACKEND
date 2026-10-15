package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.DocumentSubProduct;
import com.doritech.tmsservice.tms.entity.DocumentSubProduct.DocumentSubProductId;

public interface DocumentSubProductRepository extends JpaRepository<DocumentSubProduct, DocumentSubProductId> {

	List<DocumentSubProduct> findByIdSubProductId(Long subProductId);

	List<DocumentSubProduct> findByIdDocumentId(Long documentId);

	boolean existsByIdDocumentId(Long id);

	@Query("SELECT d.id.subProductId " + "FROM DocumentSubProduct d " + "WHERE d.id.documentId = :documentId")
	List<Long> getSubProductIdsByDocumentId(@Param("documentId") Long documentId);

}