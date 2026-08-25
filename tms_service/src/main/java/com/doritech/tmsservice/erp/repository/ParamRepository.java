package com.doritech.tmsservice.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.erp.entity.ParamEntity;

@Repository
public interface ParamRepository extends JpaRepository<ParamEntity, Integer> {

	Optional<ParamEntity> findByCodeAndSerial(@Param("code") String code, @Param("serial") String serial);

	List<ParamEntity> findByCodeIgnoreCaseAndSerialIgnoreCaseAndDesp3IgnoreCase(String code, String serial,
			String string, Sort by);

	Optional<ParamEntity> findTopByCodeIgnoreCaseAndSerialIgnoreCaseAndDesp3IgnoreCaseOrderBySerialNoDesc(String code,
			String serial, String desp3);

	Optional<ParamEntity> findByDesp1IgnoreCase(String prefix);

}