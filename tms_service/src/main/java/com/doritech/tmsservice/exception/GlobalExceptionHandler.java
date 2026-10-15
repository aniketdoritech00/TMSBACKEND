package com.doritech.tmsservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity handleBadRequest(BadRequestException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), null);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity handleResourceNotFound(ResourceNotFoundException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND.value(), null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		return new ResponseEntity("Database constraint violation. " + "The requested operation cannot be completed.",
				HttpStatus.CONFLICT.value(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity handleValidationException(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst()
				.orElse("Validation failed");
		return new ResponseEntity(message, HttpStatus.BAD_REQUEST.value(), null);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity handleConstraintViolation(ConstraintViolationException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), null);
	}

	@ExceptionHandler(DatabaseOperationException.class)
	public ResponseEntity handleDatabaseOperation(DatabaseOperationException ex) {
		return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleGenericException(Exception ex) {
		return new ResponseEntity("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
		return new ResponseEntity(e.getParameterName() + " is required", HttpStatus.BAD_REQUEST.value(), null);
	}
	
}
