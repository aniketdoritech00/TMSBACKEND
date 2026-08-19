package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class ProductResponse {

    private Long productId;
    private Long productCategoryId;
    private String productName;
    private String productCode;
    private String productDescription;
    private String productImageUrl;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse() {
    }

    public ProductResponse(Long productId, Long productCategoryId, String productName, String productCode,
            String productDescription, String productImageUrl, Integer displayOrder, Boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.productCategoryId = productCategoryId;
        this.productName = productName;
        this.productCode = productCode;
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
        return "ProductResponse [productId=" + productId
                + ", productCategoryId=" + productCategoryId
                + ", productName=" + productName
                + ", productCode=" + productCode
                + ", productDescription=" + productDescription
                + ", productImageUrl=" + productImageUrl
                + ", displayOrder=" + displayOrder
                + ", isActive=" + isActive
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }
}