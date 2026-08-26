package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.response.ParamResponseDTO;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface ParamService {
	List<ParamResponseDTO> getByCodeAndSerial(String code, String serial);

	ResponseEntity generateCode(String type);
	
	ResponseEntity updateCodeValue(String code);

	ResponseEntity updateCodeValueOnDelete(String code);
}