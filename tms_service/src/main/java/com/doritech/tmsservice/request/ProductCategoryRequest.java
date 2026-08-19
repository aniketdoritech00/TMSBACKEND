package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductCategoryRequest {

    @NotNull(message = "{productCategory.name.notnull}")
    @NotBlank(message = "{productCategory.name.notblank}")
    @Size(min = 2, max = 255, message = "{productCategory.name.size}")
    private String productCategoryName;

    @NotNull(message = "{productCategory.code.notnull}")
    @NotBlank(message = "{productCategory.code.notblank}")
    @Size(min = 2, max = 50, message = "{productCategory.code.size}")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "{productCategory.code.pattern}")
    private String productCategoryCode;

    @Size(max = 2000, message = "{productCategory.description.size}")
    private String productCategoryDescription;

    @Size(max = 500, message = "{productCategory.imageUrl.size}")
    @Pattern(
        regexp = "^(https?://).+$",
        message = "{productCategory.imageUrl.pattern}"
    )
    private String productCategoryImageUrl;

    @NotNull(message = "{productCategory.displayOrder.notnull}")
    @Min(value = 0, message = "{productCategory.displayOrder.min}")
    private Integer productCategoryDisplayOrder;

    @NotNull(message = "{productCategory.isActive.notnull}")
    private Boolean isActive;

    public ProductCategoryRequest() {
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

    public String getProductCategoryImageUrl() {
        return productCategoryImageUrl;
    }

    public void setProductCategoryImageUrl(String productCategoryImageUrl) {
        this.productCategoryImageUrl = productCategoryImageUrl;
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

    @Override
    public String toString() {
        return "ProductCategoryRequest [productCategoryName=" + productCategoryName
                + ", productCategoryCode=" + productCategoryCode
                + ", productCategoryDescription=" + productCategoryDescription
                + ", productCategoryImageUrl=" + productCategoryImageUrl
                + ", productCategoryDisplayOrder=" + productCategoryDisplayOrder
                + ", isActive=" + isActive + "]";
    }
}