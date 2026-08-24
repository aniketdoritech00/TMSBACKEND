package com.doritech.tmsservice.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TrainingCategoryRequest {

    @NotNull(message = "{trainingCategory.name.notnull}")
    @NotBlank(message = "{trainingCategory.name.notblank}")
    @Size(min = 2, max = 100, message = "{trainingCategory.name.size}")
    private String categoryName;

    @Size(max = 2000, message = "{trainingCategory.description.size}")
    private String categoryDescription;

    @Size(max = 50, message = "{trainingCategory.code.size}")
    @Pattern(regexp = "^[A-Z0-9_-]*$", message = "{trainingCategory.code.pattern}")
    private String categoryCode;

    public TrainingCategoryRequest() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Override
    public String toString() {
        return "TrainingCategoryRequest [categoryName=" + categoryName
                + ", categoryCode=" + categoryCode + "]";
    }
}