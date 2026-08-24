package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class ProductCategoryResponse {

	private Long productCategoryId;
	private String productCategoryName;
	private String productCategoryCode;
	private String productCategoryDescription;
	private Integer productCategoryDisplayOrder;
	private Boolean isActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ProductCategoryResponse() {
	}

	public ProductCategoryResponse(Long productCategoryId, String productCategoryName, String productCategoryCode,
			String productCategoryDescription, String productCategoryImageUrl, Integer productCategoryDisplayOrder,
			Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.productCategoryId = productCategoryId;
		this.productCategoryName = productCategoryName;
		this.productCategoryCode = productCategoryCode;
		this.productCategoryDescription = productCategoryDescription;
		this.productCategoryDisplayOrder = productCategoryDisplayOrder;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Long productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public String getProductCategoryCode() {
		return productCategoryCode;
	}

	public void setProductCategoryCode(String productCategoryCode) {
		this.productCategoryCode = productCategoryCode;
	}

	public String getProductCategoryDescription() {
		return productCategoryDescription;
	}

	public void setProductCategoryDescription(String productCategoryDescription) {
		this.productCategoryDescription = productCategoryDescription;
	}

	public Integer getProductCategoryDisplayOrder() {
		return productCategoryDisplayOrder;
	}

	public void setProductCategoryDisplayOrder(Integer productCategoryDisplayOrder) {
		this.productCategoryDisplayOrder = productCategoryDisplayOrder;
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
		return "ProductCategoryResponse [productCategoryId=" + productCategoryId + ", productCategoryName="
				+ productCategoryName + ", productCategoryCode=" + productCategoryCode + ", productCategoryDescription="
				+ productCategoryDescription + ", productCategoryDisplayOrder=" + productCategoryDisplayOrder
				+ ", isActive=" + isActive + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}