package com.doritech.tmsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.entity.UserBatch;

public interface UserBatchRepository extends JpaRepository<UserBatch, Long> {

}
