package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class SubProductResponse {

    private Long subProductId;
    private Long productId;
    private String subProductName;
    private String subProductCode;
    private String subProductDescription;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SubProductResponse() {
    }

    public SubProductResponse(Long subProductId, Long productId, String subProductName, String subProductCode,
            String subProductDescription, Integer displayOrder, Boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.subProductId = subProductId;
        this.productId = productId;
        this.subProductName = subProductName;
        this.subProductCode = subProductCode;
        this.subProductDescription = subProductDescription;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
        return "SubProductResponse [subProductId=" + subProductId
                + ", productId=" + productId
                + ", subProductName=" + subProductName
                + ", subProductCode=" + subProductCode
                + ", subProductDescription=" + subProductDescription
                + ", displayOrder=" + displayOrder
                + ", isActive=" + isActive
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }
}