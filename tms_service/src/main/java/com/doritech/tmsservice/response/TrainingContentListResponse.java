package com.doritech.tmsservice.response;

public class TrainingContentListResponse {

    private Long trainingContentId;
    private Long trainingId;
    private String contentType;
    private Integer displayOrder;
    private Boolean isRequired;

    public TrainingContentListResponse() {
    }

    public TrainingContentListResponse(Long trainingContentId, Long trainingId, String contentType,
            Integer displayOrder, Boolean isRequired) {
        this.trainingContentId = trainingContentId;
        this.trainingId = trainingId;
        this.contentType = contentType;
        this.displayOrder = displayOrder;
        this.isRequired = isRequired;
    }

    public Long getTrainingContentId() {
        return trainingContentId;
    }

    public void setTrainingContentId(Long trainingContentId) {
        this.trainingContentId = trainingContentId;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
}