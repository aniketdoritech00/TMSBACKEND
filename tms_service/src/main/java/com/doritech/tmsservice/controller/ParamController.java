package com.doritech.tmsservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.response.ApiResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/tms/param")
public class ParamController {

	private final ParamService paramService;

	public ParamController(ParamService paramService) {
		this.paramService = paramService;
	}

	@GetMapping("/getParamByCodeAndSerial")
	public ResponseEntity getByCode(@RequestParam String code, @RequestParam String serial,
			HttpServletRequest request) {

		ApiResponse<Object> response = new ApiResponse<>();
		response.setSuccess(true);
		response.setMessage("Param list fetched successfully");
		response.setData(paramService.getByCodeAndSerial(code, serial));
		response.setStatusCode(HttpStatus.OK.value());
		response.setPath(request.getRequestURI());

		return new ResponseEntity("Fetched Successfully", HttpStatus.OK.value(), response);
	}
	
	@GetMapping("/generate-code")
	public ResponseEntity generateCode(@RequestParam String serial) {
		return paramService.generateCode(serial);
	}
}
