package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_categories")
public class ProductCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_category_id")
	private Long productCategoryId;

	@Column(name = "product_category_name", nullable = false, length = 255)
	private String productCategoryName;

	@Column(name = "product_category_code", unique = true, length = 50)
	private String productCategoryCode;

	@Column(name = "product_category_description", columnDefinition = "TEXT")
	private String productCategoryDescription;

	@Column(name = "product_category_display_order")
	private Integer productCategoryDisplayOrder = 0;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "updated_by")
	private Long updatedBy;

	@OneToMany(mappedBy = "productCategory")
	@JsonIgnore
	private List<Product> products = new ArrayList<>();

	public ProductCategory() {
	}

	@PrePersist
	protected void onCreate() {

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		if (this.productCategoryDisplayOrder == null) {
			this.productCategoryDisplayOrder = 0;
		}

		if (this.isActive == null) {
			this.isActive = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
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

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
