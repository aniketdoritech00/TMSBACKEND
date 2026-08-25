package com.doritech.tmsservice.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}