package com.doritech.tmsservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "sub_products", uniqueConstraints = @UniqueConstraint(columnNames = { "product_id", "sub_product_name" }))
public class SubProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sub_product_id")
	private Long subProductId;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(name = "sub_product_name", nullable = false, length = 255)
	private String subProductName;

	@Column(name = "sub_product_code", unique = true, length = 50)
	private String subProductCode;

	@Column(name = "sub_product_description", columnDefinition = "TEXT")
	private String subProductDescription;

	@Column(name = "display_order")
	private Integer displayOrder = 0;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public SubProduct() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.displayOrder == null) {
			this.displayOrder = 0;
		}
		if (this.isActive == null) {
			this.isActive = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getSubProductId() {
		return subProductId;
	}

	public void setSubProductId(Long subProductId) {
		this.subProductId = subProductId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getSubProductName() {
		return subProductName;
	}

	public void setSubProductName(String subProductName) {
		this.subProductName = subProductName;
	}

	public String getSubProductCode() {
		return subProductCode;
	}

	public void setSubProductCode(String subProductCode) {
		this.subProductCode = subProductCode;
	}

	public String getSubProductDescription() {
		return subProductDescription;
	}

	public void setSubProductDescription(String subProductDescription) {
		this.subProductDescription = subProductDescription;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "SubProduct [subProductId=" + subProductId + ", productId=" + productId + ", subProductName="
				+ subProductName + ", subProductCode=" + subProductCode + ", subProductDescription="
				+ subProductDescription + ", displayOrder=" + displayOrder + ", isActive=" + isActive + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}
}