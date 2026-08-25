package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.UserResponse;

@Repository
public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {

	List<UserResponse> findByTestAttempt_TestAttemptId(Long testAttemptId);

	Optional<UserResponse> findByTestAttempt_TestAttemptIdAndTestQuestion_TestQuestionId(Long testAttemptId,
			Long testQuestionId);

	boolean existsByTestAttempt_TestAttemptIdAndTestQuestion_TestQuestionId(Long testAttemptId, Long testQuestionId);

	long countByTestAttempt_TestAttemptId(Long testAttemptId);

	long countByTestAttempt_TestAttemptIdAndIsCorrect(Long testAttemptId, Boolean isCorrect);

	List<UserResponse> findByTestAttempt_TestAttemptIdAndIsCorrect(Long testAttemptId, Boolean isCorrect);

	void deleteByTestAttempt_TestAttemptId(Long testAttemptId);
}