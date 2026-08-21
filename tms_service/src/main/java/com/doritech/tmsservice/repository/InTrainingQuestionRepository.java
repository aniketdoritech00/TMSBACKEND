package com.doritech.tmsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.InTrainingQuestion;

@Repository
public interface InTrainingQuestionRepository extends JpaRepository<InTrainingQuestion, Long> {

	List<InTrainingQuestion> findByVideoIdOrderByTimestampSecondsAsc(Long videoId);
}