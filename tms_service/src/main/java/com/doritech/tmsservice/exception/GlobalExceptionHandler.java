package com.doritech.tmsservice.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.doritech.tmsservice.entity.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BadRequestException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleBadRequest(BadRequestException ex) {
		logger.error("Bad Request: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleIllegalArgument(IllegalArgumentException ex) {
		logger.error("Illegal Argument: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	@ExceptionHandler(IllegalStateException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleIllegalState(IllegalStateException ex) {
		logger.error("Illegal State: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	@ExceptionHandler(ExcelValidationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleExcelValidation(ExcelValidationException ex) {
		logger.error("Excel Validation Error: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	@ExceptionHandler(NumberFormatException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleNumberFormat(NumberFormatException ex) {
		logger.error("Number Format Error: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Invalid numeric value: " + ex.getMessage(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex) {
		logger.error("Validation Error: {}", ex.getMessage());
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage())
				.toList();
		return build(HttpStatus.BAD_REQUEST, "Validation Failed", errors);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleHandlerMethodValidation(
			HandlerMethodValidationException ex) {
		logger.error("Handler Method Validation Error: {}", ex.getMessage());
		String errorMessage = ex.getAllErrors().stream().map(error -> error.getDefaultMessage()).findFirst()
				.orElse("Validation failed");
		return build(HttpStatus.BAD_REQUEST, errorMessage, null);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleConstraintViolation(
			ConstraintViolationException ex) {
		logger.error("Constraint Violation: {}", ex.getMessage());
		List<String> errors = ex.getConstraintViolations().stream()
				.map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
		return build(HttpStatus.BAD_REQUEST, "Constraint Violation", errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex) {
		logger.error("Malformed Request Body: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Invalid or malformed request body", null);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMissingHeader(
			MissingRequestHeaderException ex) {
		logger.error("Missing Header: {}", ex.getHeaderName());
		return build(HttpStatus.BAD_REQUEST, "Required header '" + ex.getHeaderName() + "' is missing", null);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMissingParam(
			MissingServletRequestParameterException ex) {
		logger.error("Missing Request Parameter: {}", ex.getParameterName());
		return build(HttpStatus.BAD_REQUEST, "Required parameter '" + ex.getParameterName() + "' is missing", null);
	}

	@ExceptionHandler(MissingPathVariableException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMissingPathVariable(
			MissingPathVariableException ex) {
		logger.error("Missing Path Variable: {}", ex.getVariableName());
		return build(HttpStatus.BAD_REQUEST, "Required path variable '" + ex.getVariableName() + "' is missing", null);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMissingRequestPart(
			MissingServletRequestPartException ex) {
		logger.error("Missing Request Part: {}", ex.getRequestPartName());
		return build(HttpStatus.BAD_REQUEST, "Required request part '" + ex.getRequestPartName() + "' is missing",
				null);
	}

	@ExceptionHandler(ServletRequestBindingException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleServletRequestBinding(
			ServletRequestBindingException ex) {
		logger.error("Request Binding Error: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Request binding failed: " + ex.getMessage(), null);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleTypeMismatch(
			MethodArgumentTypeMismatchException ex) {
		logger.error("Type Mismatch: parameter '{}' value '{}'", ex.getName(), ex.getValue());
		String message = "Parameter '" + ex.getName() + "' must be of type '"
				+ (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown") + "'";
		return build(HttpStatus.BAD_REQUEST, message, null);
	}

	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleInvalidDataAccessApiUsage(
			InvalidDataAccessApiUsageException ex) {
		logger.error("Invalid Data Access API Usage: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Invalid query or repository operation: " + ex.getMessage(), null);
	}

	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleInvalidDataAccessResourceUsage(
			InvalidDataAccessResourceUsageException ex) {
		logger.error("Invalid Data Access Resource Usage: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Invalid database resource usage: " + ex.getMessage(), null);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleResourceNotFound(
			ResourceNotFoundException ex) {
		logger.error("Resource Not Found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleNoResourceFound(NoResourceFoundException ex) {
		logger.error("API Endpoint Not Found: {}", ex.getResourcePath());
		return build(HttpStatus.NOT_FOUND, "API endpoint not found: " + ex.getResourcePath(), null);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleEntityNotFound(EntityNotFoundException ex) {
		logger.error("Entity Not Found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
	}

	@ExceptionHandler(JpaObjectRetrievalFailureException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleJpaObjectRetrieval(
			JpaObjectRetrievalFailureException ex) {
		logger.error("JPA Object Not Found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, "Requested record not found", null);
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleEmptyResult(
			EmptyResultDataAccessException ex) {
		logger.error("Empty Result: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, "No record found for given input", null);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMethodNotSupported(
			HttpRequestMethodNotSupportedException ex) {
		logger.error("Method Not Allowed: {}", ex.getMessage());
		String message = "HTTP method '" + ex.getMethod() + "' is not supported. Supported: "
				+ ex.getSupportedHttpMethods();
		return build(HttpStatus.METHOD_NOT_ALLOWED, message, null);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMediaTypeNotAcceptable(
			HttpMediaTypeNotAcceptableException ex) {
		logger.error("Media Type Not Acceptable: {}", ex.getMessage());
		return build(HttpStatus.NOT_ACCEPTABLE, "Requested response format is not supported", null);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleAlreadyExists(
			ResourceAlreadyExistsException ex) {
		logger.error("Resource Already Exists: {}", ex.getMessage());
		return build(HttpStatus.CONFLICT, ex.getMessage(), null);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleDuplicateResource(
			DuplicateResourceException ex) {
		logger.error("Duplicate Resource: {}", ex.getMessage());
		return build(HttpStatus.CONFLICT, ex.getMessage(), null);
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleDuplicateKey(DuplicateKeyException ex) {
		logger.error("Duplicate Key: {}", ex.getMessage());
		return build(HttpStatus.CONFLICT, "Duplicate value: a record with this key already exists", null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleDataIntegrityViolation(
			DataIntegrityViolationException ex) {
		logger.error("Data Integrity Violation: {}", ex.getMostSpecificCause().getMessage());
		String cause = ex.getMostSpecificCause().getMessage();
		String message;
		if (cause != null && cause.toLowerCase().contains("foreign key")) {
			message = "Cannot delete or update: record is referenced by another table";
		} else if (cause != null
				&& (cause.toLowerCase().contains("unique") || cause.toLowerCase().contains("duplicate"))) {
			message = "Duplicate value: a record with this data already exists";
		} else {
			message = "Data integrity violation: " + cause;
		}
		return build(HttpStatus.CONFLICT, message, null);
	}

	@ExceptionHandler(OptimisticLockingFailureException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleOptimisticLocking(
			OptimisticLockingFailureException ex) {
		logger.error("Optimistic Locking Failure: {}", ex.getMessage());
		return build(HttpStatus.CONFLICT, "Record was modified by another user. Please refresh and try again", null);
	}

	@ExceptionHandler(IncorrectResultSizeDataAccessException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleIncorrectResultSize(
			IncorrectResultSizeDataAccessException ex) {
		logger.error("Incorrect Result Size: {}", ex.getMessage());
		return build(HttpStatus.CONFLICT, "Unexpected number of records found", null);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMaxUploadSize(
			MaxUploadSizeExceededException ex) {
		logger.error("Max Upload Size Exceeded: {}", ex.getMessage());
		return build(HttpStatus.PAYLOAD_TOO_LARGE, "Uploaded file size exceeds the allowed limit", null);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex) {
		logger.error("Unsupported Media Type: {}", ex.getMessage());
		String message = "Content-Type '" + ex.getContentType() + "' is not supported. Use 'application/json'";
		return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message, null);
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleUnsupportedOperation(
			UnsupportedOperationException ex) {
		logger.error("Unsupported Operation: {}", ex.getMessage());
		return build(HttpStatus.NOT_IMPLEMENTED, "This operation is not supported", null);
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleHttpClientError(
			HttpClientErrorException ex) {
		logger.error("External Service Client Error [{}]: {}", ex.getStatusCode(), ex.getMessage());
		return build(HttpStatus.BAD_GATEWAY, "External service returned an error: " + ex.getStatusText(), null);
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleHttpServerError(
			HttpServerErrorException ex) {
		logger.error("External Service Server Error [{}]: {}", ex.getStatusCode(), ex.getMessage());
		return build(HttpStatus.BAD_GATEWAY, "External service encountered an error. Please try again later", null);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleResourceAccess(
			ResourceAccessException ex) {
		logger.error("External Resource Access Error: {}", ex.getMessage());
		return build(HttpStatus.BAD_GATEWAY, "Unable to reach external service. Please try again later", null);
	}

	@ExceptionHandler(ExternalServiceException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleExternalService(ExternalServiceException ex) {
		logger.error("External Service Error: {}", ex.getMessage());
		return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), null);
	}

	@ExceptionHandler(ConnectException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleConnectException(ConnectException ex) {
		logger.error("Connection Failed: {}", ex.getMessage());
		return build(HttpStatus.BAD_GATEWAY, "Unable to connect to an external service. Please try again later", null);
	}

	@ExceptionHandler(SocketTimeoutException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleSocketTimeout(SocketTimeoutException ex) {
		logger.error("Socket Timeout: {}", ex.getMessage());
		return build(HttpStatus.GATEWAY_TIMEOUT, "External service did not respond in time. Please try again later",
				null);
	}

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleAsyncTimeout(AsyncRequestTimeoutException ex) {
		logger.error("Async Request Timeout: {}", ex.getMessage());
		return build(HttpStatus.SERVICE_UNAVAILABLE, "Request timed out. Please try again later", null);
	}

	@ExceptionHandler(TransactionTimedOutException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleTransactionTimeout(
			TransactionTimedOutException ex) {
		logger.error("Transaction Timed Out: {}", ex.getMessage());
		return build(HttpStatus.SERVICE_UNAVAILABLE, "Transaction timed out. Please try again later", null);
	}

	@ExceptionHandler(PessimisticLockingFailureException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handlePessimisticLocking(
			PessimisticLockingFailureException ex) {
		logger.error("Database Lock/Deadlock issue: {}", ex.getMessage());
		return build(HttpStatus.SERVICE_UNAVAILABLE, "Database is busy or locked. Please try again.", null);
	}

	@ExceptionHandler(CannotAcquireLockException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleCannotAcquireLock(
			CannotAcquireLockException ex) {
		logger.error("Cannot Acquire Lock: {}", ex.getMessage());
		return build(HttpStatus.SERVICE_UNAVAILABLE, "Database is busy. Please try again later", null);
	}

	@ExceptionHandler(QueryTimeoutException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleQueryTimeout(QueryTimeoutException ex) {
		logger.error("Query Timeout: {}", ex.getMessage());
		return build(HttpStatus.SERVICE_UNAVAILABLE, "Database query timed out. Please try again later", null);
	}

	@ExceptionHandler(DatabaseOperationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleDatabase(DatabaseOperationException ex) {
		logger.error("Database Error: {}", ex.getMessage());
		String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
		return build(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
	}

	@ExceptionHandler(InternalServerException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleInternalServer(InternalServerException ex) {
		logger.error("Internal Server Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleTransaction(TransactionSystemException ex) {
		logger.error("Transaction Failed: {}", ex.getMessage());
		Throwable cause = ex.getRootCause();
		if (cause instanceof ConstraintViolationException cve) {
			List<String> errors = cve.getConstraintViolations().stream()
					.map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
			return build(HttpStatus.BAD_REQUEST, "Validation failed on transaction commit", errors);
		}
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction failed. Please try again", null);
	}

	@ExceptionHandler(JpaSystemException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleJpaSystem(JpaSystemException ex) {
		logger.error("JPA System Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "A JPA/Hibernate error occurred. Please try again", null);
	}

	@ExceptionHandler(PersistenceException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handlePersistence(PersistenceException ex) {
		logger.error("Persistence Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "A database persistence error occurred. Please try again", null);
	}

	@ExceptionHandler(ApplicationException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleApplicationException(ApplicationException ex) {
		logger.error("Application Exception [ErrorCode: {}]: {}", ex.getErrorCode(), ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "[" + ex.getErrorCode() + "] " + ex.getMessage(), null);
	}

	@ExceptionHandler(HttpMessageNotWritableException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleHttpMessageNotWritable(
			HttpMessageNotWritableException ex) {
		logger.error("Response Write Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error writing response. Please try again", null);
	}

	@ExceptionHandler(DataAccessException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleDataAccess(DataAccessException ex) {
		logger.error("Data Access Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred. Please try again", null);
	}

	@ExceptionHandler(NullPointerException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleNullPointer(NullPointerException ex) {
		logger.error("Null Pointer Exception: {}", ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected null value error occurred", null);
	}

	@ExceptionHandler(ArithmeticException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleArithmetic(ArithmeticException ex) {
		logger.error("Arithmetic Exception: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "A calculation error occurred: " + ex.getMessage(), null);
	}

	@ExceptionHandler(ClassCastException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleClassCast(ClassCastException ex) {
		logger.error("Class Cast Exception: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal type mismatch error occurred", null);
	}

	@ExceptionHandler(IndexOutOfBoundsException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleIndexOutOfBounds(
			IndexOutOfBoundsException ex) {
		logger.error("Index Out Of Bounds Exception: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal data access error occurred", null);
	}

	@ExceptionHandler(ArrayIndexOutOfBoundsException.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleArrayIndexOutOfBounds(
			ArrayIndexOutOfBoundsException ex) {
		logger.error("Array Index Out Of Bounds Exception: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal data access error occurred", null);
	}

	@ExceptionHandler(StackOverflowError.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleStackOverflow(StackOverflowError ex) {
		logger.error("Stack Overflow Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "A critical server error occurred", null);
	}

	@ExceptionHandler(OutOfMemoryError.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleOutOfMemory(OutOfMemoryError ex) {
		logger.error("Out Of Memory Error: {}", ex.getMessage());
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Server ran out of memory. Please try again later", null);
	}

	@ExceptionHandler(Exception.class)
	public org.springframework.http.ResponseEntity<ResponseEntity> handleAll(Exception ex) {
		logger.error("Unhandled Exception [{}]: {}", ex.getClass().getName(), ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null);
	}

	private org.springframework.http.ResponseEntity<ResponseEntity> build(HttpStatus status, String message,
			Object payload) {
		return org.springframework.http.ResponseEntity.status(status)
				.body(new ResponseEntity(message, status.value(), payload));
	}
	@ExceptionHandler(org.springframework.web.context.request.async.AsyncRequestNotUsableException.class)
	public void handleClientDisconnect(
			org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {

		logger.warn("Client disconnected before the response could be fully written: {}", e.getMessage());
	}
}