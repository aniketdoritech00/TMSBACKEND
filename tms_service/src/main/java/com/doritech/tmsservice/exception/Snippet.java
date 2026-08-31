package com.doritech.tmsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.doritech.tmsservice.tms.entity.ResponseEntity;

public class Snippet {
	@ExceptionHandler(Exception.class)
	public ResponseEntity handleGenericException(Exception e) {

		return new ResponseEntity("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
	}
}
