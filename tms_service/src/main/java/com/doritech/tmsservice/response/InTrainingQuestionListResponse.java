package com.doritech.tmsservice.response;

public class InTrainingQuestionListResponse {

    private Long questionId;
    private Long videoId;
    private Integer timestampSeconds;
    private String questionType;
    private Integer displayOrder;

    public InTrainingQuestionListResponse() {
    }

    public InTrainingQuestionListResponse(Long questionId, Long videoId, Integer timestampSeconds,
            String questionType, Integer displayOrder) {
        this.questionId = questionId;
        this.videoId = videoId;
        this.timestampSeconds = timestampSeconds;
        this.questionType = questionType;
        this.displayOrder = displayOrder;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Integer getTimestampSeconds() {
        return timestampSeconds;
    }

    public void setTimestampSeconds(Integer timestampSeconds) {
        this.timestampSeconds = timestampSeconds;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}