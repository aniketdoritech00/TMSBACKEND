package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.InTrainingQuestion;

@Repository
public interface InTrainingQuestionRepository extends JpaRepository<InTrainingQuestion, Long> {

	List<InTrainingQuestion> findByVideoIdOrderByTimestampSecondsAsc(Long videoId);
}