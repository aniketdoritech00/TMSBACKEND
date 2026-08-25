package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_responses")
public class UserResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_response_id")
	private Long userResponseId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_attempt_id", nullable = false)
	private TestAttempt testAttempt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_question_id", nullable = false)
	private TestQuestion testQuestion;

	@Column(name = "user_answer", length = 255)
	private String userAnswer;

	@Column(name = "is_correct", columnDefinition = "boolean default false")
	private Boolean isCorrect = false;

	@Column(name = "time_taken_seconds")
	private Integer timeTakenSeconds;

	@CreationTimestamp
	@Column(name = "response_date", updatable = false)
	private LocalDateTime responseDate;

	public Long getUserResponseId() {
		return userResponseId;
	}

	public void setUserResponseId(Long userResponseId) {
		this.userResponseId = userResponseId;
	}

	public TestAttempt getTestAttempt() {
		return testAttempt;
	}

	public void setTestAttempt(TestAttempt testAttempt) {
		this.testAttempt = testAttempt;
	}

	public TestQuestion getTestQuestion() {
		return testQuestion;
	}

	public void setTestQuestion(TestQuestion testQuestion) {
		this.testQuestion = testQuestion;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Integer getTimeTakenSeconds() {
		return timeTakenSeconds;
	}

	public void setTimeTakenSeconds(Integer timeTakenSeconds) {
		this.timeTakenSeconds = timeTakenSeconds;
	}

	public LocalDateTime getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(LocalDateTime responseDate) {
		this.responseDate = responseDate;
	}

}