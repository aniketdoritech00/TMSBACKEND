package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.InTrainingQuestion;

public interface InTrainingQuestionRepository extends JpaRepository<InTrainingQuestion, Long> {

	boolean existsByVideo_VideoIdAndTimestampSecondsAndQuestionText(Long videoId, Integer timestampSeconds,
			String questionText);

	boolean existsByVideo_VideoIdAndTimestampSecondsAndQuestionTextAndQuestionIdNot(Long videoId,
			Integer timestampSeconds, String questionText, Long questionId);

	List<InTrainingQuestion> findByVideo_VideoIdOrderByDisplayOrderAsc(Long videoId);

	Page<InTrainingQuestion> findByVideo_VideoId(Long videoId, Pageable pageable);
}