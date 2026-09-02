package com.doritech.tmsservice.tms.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TrainingSubProductId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "training_id")
    private Long trainingId;

    @Column(name = "sub_product_id")
    private Long subProductId;

    public TrainingSubProductId() {
    }

    public TrainingSubProductId(Long trainingId, Long subProductId) {
        this.trainingId = trainingId;
        this.subProductId = subProductId;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public Long getSubProductId() {
        return subProductId;
    }

    public void setSubProductId(Long subProductId) {
        this.subProductId = subProductId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof TrainingSubProductId)) {
            return false;
        }

        TrainingSubProductId that = (TrainingSubProductId) o;

        return Objects.equals(trainingId, that.trainingId)
                && Objects.equals(subProductId, that.subProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingId, subProductId);
    }
}