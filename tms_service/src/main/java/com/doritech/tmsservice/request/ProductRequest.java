package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductRequest {

	@NotNull(message = "{product.categoryId.notnull}")
	private Long productCategoryId;

	@NotNull(message = "{product.name.notnull}")
	@NotBlank(message = "{product.name.notblank}")
	@Size(min = 2, max = 255, message = "{product.name.size}")
	private String productName;

	@NotNull(message = "{product.code.notnull}")
	@NotBlank(message = "{product.code.notblank}")
	@Size(min = 2, max = 50, message = "{product.code.size}")
	@Pattern(regexp = "^[A-Z0-9_-]+$", message = "{product.code.pattern}")
	private String productCode;

	@Size(max = 2000, message = "{product.description.size}")
	private String productDescription;

	@Size(max = 500, message = "{product.imageUrl.size}")
	@Pattern(regexp = "^(https?://).+$", message = "{product.imageUrl.pattern}")
	private String productImageUrl;

	@NotNull(message = "{product.displayOrder.notnull}")
	@Min(value = 0, message = "{product.displayOrder.min}")
	private Integer displayOrder;

	@NotNull(message = "{product.isActive.notnull}")
	private Boolean isActive;

	public ProductRequest() {
	}

	public Long getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Long productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductImageUrl() {
		return productImageUrl;
	}

	public void setProductImageUrl(String productImageUrl) {
		this.productImageUrl = productImageUrl;
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

	@Override
	public String toString() {
		return "ProductRequest [productCategoryId=" + productCategoryId + ", productName=" + productName
				+ ", productCode=" + productCode + ", productDescription=" + productDescription + ", productImageUrl="
				+ productImageUrl + ", displayOrder=" + displayOrder + ", isActive=" + isActive + "]";
	}
}