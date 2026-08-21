package com.doritech.tmsservice.response;

public class DocumentListResponse {

    private Long documentId;
    private String documentName;
    private String documentType;
    private Boolean isSecure;

    public DocumentListResponse() {
    }

    public DocumentListResponse(Long documentId, String documentName, String documentType, Boolean isSecure) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentType = documentType;
        this.isSecure = isSecure;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Boolean getIsSecure() {
        return isSecure;
    }

    public void setIsSecure(Boolean isSecure) {
        this.isSecure = isSecure;
    }
}