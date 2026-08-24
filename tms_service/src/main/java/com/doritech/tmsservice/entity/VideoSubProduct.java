package com.doritech.tmsservice.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_sub_products")
public class VideoSubProduct {

    @EmbeddedId
    private VideoSubProductId id;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private Long assignedBy;

    public VideoSubProduct() {
    }

    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDateTime.now();
    }

    public VideoSubProductId getId() {
        return id;
    }

    public void setId(VideoSubProductId id) {
        this.id = id;
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

    @Embeddable
    public static class VideoSubProductId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "video_id")
        private Long videoId;

        @Column(name = "sub_product_id")
        private Long subProductId;

        public VideoSubProductId() {
        }

        public VideoSubProductId(Long videoId, Long subProductId) {
            this.videoId = videoId;
            this.subProductId = subProductId;
        }

        public Long getVideoId() {
            return videoId;
        }

        public void setVideoId(Long videoId) {
            this.videoId = videoId;
        }

        public Long getSubProductId() {
            return subProductId;
        }

        public void setSubProductId(Long subProductId) {
            this.subProductId = subProductId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VideoSubProductId)) return false;
            VideoSubProductId that = (VideoSubProductId) o;
            return Objects.equals(videoId, that.videoId) && Objects.equals(subProductId, that.subProductId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(videoId, subProductId);
        }
    }
}