package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SubProductRequest {

    @NotNull(message = "{subProduct.productId.notnull}")
    private Long productId;

    @NotNull(message = "{subProduct.name.notnull}")
    @NotBlank(message = "{subProduct.name.notblank}")
    @Size(min = 2, max = 255, message = "{subProduct.name.size}")
    private String subProductName;

    @NotNull(message = "{subProduct.code.notnull}")
    @NotBlank(message = "{subProduct.code.notblank}")
    @Size(min = 2, max = 50, message = "{subProduct.code.size}")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "{subProduct.code.pattern}")
    private String subProductCode;

    @Size(max = 2000, message = "{subProduct.description.size}")
    private String subProductDescription;

    @NotNull(message = "{subProduct.displayOrder.notnull}")
    @Min(value = 0, message = "{subProduct.displayOrder.min}")
    private Integer displayOrder;

    @NotNull(message = "{subProduct.isActive.notnull}")
    private Boolean isActive;

    public SubProductRequest() {
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

    @Override
    public String toString() {
        return "SubProductRequest [productId=" + productId
                + ", subProductName=" + subProductName
                + ", subProductCode=" + subProductCode
                + ", subProductDescription=" + subProductDescription
                + ", displayOrder=" + displayOrder
                + ", isActive=" + isActive + "]";
    }
}