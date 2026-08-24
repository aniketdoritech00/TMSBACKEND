package com.doritech.tmsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}