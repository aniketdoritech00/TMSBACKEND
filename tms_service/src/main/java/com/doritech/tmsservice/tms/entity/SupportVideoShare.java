package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.SupportVideoShareStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "support_video_shares")
public class SupportVideoShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_video_share_id")
    private Long supportVideoShareId;

    @Column(name = "support_request_id", nullable = false)
    private Long supportRequestId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shared_by", nullable = false)
    private Long sharedBy;

    @Column(name = "shared_at")
    private LocalDateTime sharedAt;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        columnDefinition =
            "enum('SHARED','VIEWED','COMPLETED') default 'SHARED'"
    )
    private SupportVideoShareStatus status =
            SupportVideoShareStatus.SHARED;

    public SupportVideoShare() {
    }

    public Long getSupportVideoShareId() {
        return supportVideoShareId;
    }

    public void setSupportVideoShareId(
            Long supportVideoShareId) {
        this.supportVideoShareId =
                supportVideoShareId;
    }

    public Long getSupportRequestId() {
        return supportRequestId;
    }

    public void setSupportRequestId(
            Long supportRequestId) {
        this.supportRequestId =
                supportRequestId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(Long sharedBy) {
        this.sharedBy = sharedBy;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }

    public SupportVideoShareStatus getStatus() {
        return status;
    }

    public void setStatus(
            SupportVideoShareStatus status) {
        this.status = status;
    }
}