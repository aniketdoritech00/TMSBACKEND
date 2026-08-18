package com.doritech.microservice.AuthenticationService.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.microservice.AuthenticationService.Entity.EmployeeMaster;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMaster, Integer> {

	Optional<EmployeeMaster> findByEmployeeId(Integer employeeId);

}