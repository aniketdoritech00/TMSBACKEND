package com.doritech.tmsservice.tms.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.enums.SecurityViolationType;
import com.doritech.tmsservice.tms.entity.SecurityLog;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

	@Query("""
			SELECT s
			FROM SecurityLog s
			WHERE
			    (:violationType IS NULL
			     OR s.violationType = :violationType)
			AND
			    (:warningShown IS NULL
			     OR s.warningShown = :warningShown)
			AND
			    (:fromDate IS NULL
			     OR s.timestamp >= :fromDate)
			AND
			    (:toDate IS NULL
			     OR s.timestamp <= :toDate)
			""")
	Page<SecurityLog> findSecurityLogsByFilter(@Param("violationType") SecurityViolationType violationType,
			@Param("warningShown") Boolean warningShown, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate, Pageable pageable);

}