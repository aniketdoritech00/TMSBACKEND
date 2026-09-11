package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.doritech.tmsservice.enums.TestQuestionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_questions")
public class TestQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_question_id")
	private Long testQuestionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_set_id", nullable = false)
	private TestSet testSet;

	@Column(name = "question_group_id")
	private Integer questionGroupId;

	@Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type")
	private TestQuestionType questionType = TestQuestionType.MCQ;

	@Column(name = "correct_answer", length = 255)
	private String correctAnswer;

	@Column(name = "time_limit_seconds")
	private Integer timeLimitSeconds = 60;

	@Column(name = "marks", precision = 5, scale = 2)
	private BigDecimal marks = new BigDecimal("1.00");

	@Column(name = "display_order")
	private Integer displayOrder = 0;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "testQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<QuestionOption> questionOptions = new ArrayList<>();

	public TestQuestion() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.questionType == null) {
			this.questionType = TestQuestionType.MCQ;
		}
		if (this.timeLimitSeconds == null) {
			this.timeLimitSeconds = 60;
		}
		if (this.marks == null) {
			this.marks = new BigDecimal("1.00");
		}
		if (this.displayOrder == null) {
			this.displayOrder = 0;
		}
	}

	public TestSet getTestSet() {
		return testSet;
	}

	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getTestQuestionId() {
		return testQuestionId;
	}

	public void setTestQuestionId(Long testQuestionId) {
		this.testQuestionId = testQuestionId;
	}

	public Integer getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(Integer questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public TestQuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(TestQuestionType questionType) {
		this.questionType = questionType;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Integer getTimeLimitSeconds() {
		return timeLimitSeconds;
	}

	public void setTimeLimitSeconds(Integer timeLimitSeconds) {
		this.timeLimitSeconds = timeLimitSeconds;
	}

	public BigDecimal getMarks() {
		return marks;
	}

	public void setMarks(BigDecimal marks) {
		this.marks = marks;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<QuestionOption> getQuestionOptions() {
		return questionOptions;
	}

	public void setQuestionOptions(List<QuestionOption> questionOptions) {
		this.questionOptions = questionOptions;
	}

}