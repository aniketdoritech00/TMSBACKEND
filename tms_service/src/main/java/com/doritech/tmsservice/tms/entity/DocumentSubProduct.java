package com.doritech.tmsservice.tms.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_sub_products")
public class DocumentSubProduct {

	@EmbeddedId
	private DocumentSubProductId id;

	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;

	@Column(name = "assigned_by")
	private Long assignedBy;

	public DocumentSubProduct() {
	}

	@PrePersist
	protected void onCreate() {
		this.assignedAt = LocalDateTime.now();
	}

	public DocumentSubProductId getId() {
		return id;
	}

	public void setId(DocumentSubProductId id) {
		this.id = id;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public Long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Long assignedBy) {
		this.assignedBy = assignedBy;
	}

	@Embeddable
    public static class DocumentSubProductId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "document_id")
        private Long documentId;

        @Column(name = "sub_product_id")
        private Long subProductId;

		public Long getDocumentId() {
			return documentId;
		}

		public void setDocumentId(Long documentId) {
			this.documentId = documentId;
		}

		public Long getSubProductId() {
			return subProductId;
		}

		public void setSubProductId(Long subProductId) {
			this.subProductId = subProductId;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public DocumentSubProductId(Long documentId, Long subProductId) {
			super();
			this.documentId = documentId;
			this.subProductId = subProductId;
		}

		public DocumentSubProductId() {
			super();
		}
	}
}