package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "training_sub_products")
public class TrainingSubProduct {

    @EmbeddedId
    private TrainingSubProductId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "training_id",
        referencedColumnName = "training_id",
        insertable = false,
        updatable = false
    )
    private Training training;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "sub_product_id",
        referencedColumnName = "sub_product_id",
        insertable = false,
        updatable = false
    )
    private SubProduct subProduct;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private Long assignedBy;

    public TrainingSubProduct() {
    }

    public TrainingSubProductId getId() {
        return id;
    }

    public void setId(TrainingSubProductId id) {
        this.id = id;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public SubProduct getSubProduct() {
        return subProduct;
    }

    public void setSubProduct(SubProduct subProduct) {
        this.subProduct = subProduct;
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
}