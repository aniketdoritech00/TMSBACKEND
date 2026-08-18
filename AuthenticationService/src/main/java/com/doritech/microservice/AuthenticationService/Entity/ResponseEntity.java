package com.doritech.microservice.AuthenticationService.Entity;

public class ResponseEntity {
	private String message;
	private Object statusCode;
	private Object payload;

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(Object statusCode) {
		this.statusCode = statusCode;
	}

	public Object getPayload() {
		return this.payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public ResponseEntity() {
	}

	public ResponseEntity(String message, Object statusCode, Object payload) {
		this.message = message;
		this.statusCode = statusCode;
		this.payload = payload;
	}

	public String toString() {
		return "ResponseEntity [message=" + this.message + ", statusCode=" + this.statusCode + ", payload="
				+ this.payload + "]";
	}
}
