package com.doritech.microservice.AuthenticationService.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doritech.microservice.AuthenticationService.Entity.UserMaster;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Integer> {
	Optional<UserMaster> findByLoginId(String loginId);
	
	@Query(value = "SELECT code, serial,desp1, desp2, desp3 FROM param", nativeQuery = true)
	List<Object[]> getCodeSerialDesp3();
}
