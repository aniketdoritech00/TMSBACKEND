package com.doritech.tmsservice.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.erp.entity.ParamEntity;
import com.doritech.tmsservice.erp.repository.ParamRepository;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.response.ParamResponseDTO;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@Service
public class ParamServiceImpl implements ParamService {

	private final ParamRepository repository;

	public ParamServiceImpl(ParamRepository repository) {
		this.repository = repository;
	}

	private ParamResponseDTO mapToDTO(ParamEntity entity) {
		ParamResponseDTO dto = new ParamResponseDTO();
		dto.setParamId(entity.getParamId());
		dto.setCode(entity.getCode());
		dto.setSerial(entity.getSerial());
		dto.setDesp1(entity.getDesp1());
		dto.setDesp2(entity.getDesp2());
		dto.setDesp3(entity.getDesp3());
		dto.setDesp4(entity.getDesp4());
		dto.setDesp5(entity.getDesp5());
		dto.setSerialNo(entity.getSerialNo());
		return dto;
	}

	@Override
	public List<ParamResponseDTO> getByCodeAndSerial(String code, String serial) {
		List<ParamEntity> entities = repository.findByCodeIgnoreCaseAndSerialIgnoreCaseAndDesp3IgnoreCase(code, serial,
				"Y", Sort.by(Sort.Order.asc("serialNo").nullsLast()));
		if (entities.isEmpty()) {
			throw new ResourceNotFoundException("Data not found.");
		}
		return entities.stream().map(this::mapToDTO).toList();
	}

	@Override
	public ResponseEntity generateCode(String serial) {
		ResponseEntity response = new ResponseEntity();

		try {

			if (serial == null || serial.trim().isEmpty()) {
				response.setMessage("Type cannot be null or empty");
				response.setStatusCode(400);
				return response;
			}

			Optional<ParamEntity> optional = repository
					.findTopByCodeIgnoreCaseAndSerialIgnoreCaseAndDesp3IgnoreCaseOrderBySerialNoDesc("code", serial,
							"Y");

			if (optional.isEmpty()) {
				response.setMessage("Type not found or inactive: " + serial);
				response.setStatusCode(404);
				return response;
			}

			ParamEntity param = optional.get();

			Integer currentSerial = param.getDesp2() != null ? Integer.parseInt(param.getDesp2()) : 0;

			String prefix = param.getDesp1() != null ? param.getDesp1() : "";

			String previewCode = prefix + String.format("%03d", currentSerial + 1);

			response.setMessage("Preview code fetched");
			response.setStatusCode(200);
			response.setPayload(previewCode);

		} catch (Exception e) {

			response.setMessage("Error while fetching preview code");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	@Override
	public ResponseEntity updateCodeValue(String code) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (code == null || code.trim().isEmpty()) {
				response.setMessage("Code cannot be null or empty");
				response.setStatusCode(400);
				return response;
			}

			String prefix = code.replaceAll("\\d", "");

			Optional<ParamEntity> optional = repository.findByDesp1IgnoreCase(prefix);

			if (optional.isEmpty()) {
				response.setMessage("Code not found or inactive: " + prefix);
				response.setStatusCode(404);
				return response;
			}

			ParamEntity param = optional.get();

			Integer current = param.getDesp2() != null ? Integer.parseInt(param.getDesp2()) : 0;

			Integer updated = current + 1;

			param.setDesp2(String.valueOf(updated));

			repository.save(param);

			response.setMessage("Code updated successfully");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(prefix + String.format("%03d", updated));

		} catch (NumberFormatException e) {

			response.setMessage("Invalid number format in desp2");
			response.setStatusCode(500);

		} catch (Exception e) {

			response.setMessage("Error while updating code");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

}