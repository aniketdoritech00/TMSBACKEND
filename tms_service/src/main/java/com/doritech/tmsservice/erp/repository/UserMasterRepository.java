package com.doritech.tmsservice.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.erp.entity.UserMaster;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Integer> {
	Optional<UserMaster> findByLoginId(String loginId);

	@Query(value = "SELECT code, serial,desp1, desp2, desp3 FROM param", nativeQuery = true)
	List<Object[]> getCodeSerialDesp3();

	@Query(value = """
			SELECT em.employee_name
			FROM erp_db.user_master um
			JOIN erp_db.employee_master em
			    ON em.employee_id = um.source_id
			WHERE um.user_id = :userId
			""", nativeQuery = true)
	String findEmployeeNameByUserId(@Param("userId") Integer userId);
}