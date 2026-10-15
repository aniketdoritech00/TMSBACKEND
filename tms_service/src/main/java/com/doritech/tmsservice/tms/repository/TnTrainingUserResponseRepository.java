package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.InTrainingUserResponse;

public interface TnTrainingUserResponseRepository extends JpaRepository<InTrainingUserResponse, Long> {

	List<InTrainingUserResponse> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

}
