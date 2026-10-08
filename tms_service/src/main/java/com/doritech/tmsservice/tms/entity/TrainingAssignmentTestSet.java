package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "training_assignment_test_sets", uniqueConstraints = {

		@UniqueConstraint(name = "uk_training_assignment_test_set_assignment", columnNames = {
				"training_assignment_id" })

}, indexes = {

		@Index(name = "idx_training_assignment_test_set_assignment", columnList = "training_assignment_id"),

		@Index(name = "idx_training_assignment_test_set_test_set", columnList = "test_set_id")

})
public class TrainingAssignmentTestSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_assignment_test_set_id")
	private Long trainingAssignmentTestSetId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_assignment_id", nullable = false)
	private TrainingAssignment trainingAssignment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_set_id", nullable = false)
	private TestSet testSet;

	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;

	@Column(name = "assigned_by")
	private Long assignedBy;

	public Long getTrainingAssignmentTestSetId() {
		return trainingAssignmentTestSetId;
	}

	public void setTrainingAssignmentTestSetId(Long trainingAssignmentTestSetId) {
		this.trainingAssignmentTestSetId = trainingAssignmentTestSetId;
	}

	public TrainingAssignment getTrainingAssignment() {
		return trainingAssignment;
	}

	public void setTrainingAssignment(TrainingAssignment trainingAssignment) {
		this.trainingAssignment = trainingAssignment;
	}

	public TestSet getTestSet() {
		return testSet;
	}

	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public Long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Long assignedBy) {
		this.assignedBy = assignedBy;
	}
}