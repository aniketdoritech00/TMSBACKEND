package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.response.InTrainingQuestionResultResponse;
import com.doritech.tmsservice.response.InTrainingResultResponse;
import com.doritech.tmsservice.response.TestQuestionOptionResponse;
import com.doritech.tmsservice.response.TestQuestionResultResponse;
import com.doritech.tmsservice.response.TestSetResultResponse;
import com.doritech.tmsservice.response.TrainingResultResponse;
import com.doritech.tmsservice.service.TrainingResultService;
import com.doritech.tmsservice.tms.entity.InTrainingQuestion;
import com.doritech.tmsservice.tms.entity.InTrainingUserResponse;
import com.doritech.tmsservice.tms.entity.QuestionOption;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestQuestion;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.TrainingAssignmentTestSet;
import com.doritech.tmsservice.tms.entity.UserResponse;
import com.doritech.tmsservice.tms.repository.QuestionOptionRepository;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TestQuestionRepository;
import com.doritech.tmsservice.tms.repository.TnTrainingUserResponseRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentTestSetRepository;
import com.doritech.tmsservice.tms.repository.UserResponseRepository;

@Service
public class TrainingResultServiceImpl implements TrainingResultService {

	private final TrainingAssignmentRepository trainingAssignmentRepository;
	private final TnTrainingUserResponseRepository inTrainingUserResponseRepository;
	private final TrainingAssignmentTestSetRepository trainingAssignmentTestSetRepository;
	private final TestAttemptRepository testAttemptRepository;
	private final UserResponseRepository userResponseRepository;
	private final TestQuestionRepository testQuestionRepository;
	private final QuestionOptionRepository questionOptionRepository;

	public TrainingResultServiceImpl(
			TrainingAssignmentRepository trainingAssignmentRepository,
			TnTrainingUserResponseRepository inTrainingUserResponseRepository,
			TrainingAssignmentTestSetRepository trainingAssignmentTestSetRepository,
			TestAttemptRepository testAttemptRepository,
			UserResponseRepository userResponseRepository,
			TestQuestionRepository testQuestionRepository,
			QuestionOptionRepository questionOptionRepository) {

		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.inTrainingUserResponseRepository = inTrainingUserResponseRepository;
		this.trainingAssignmentTestSetRepository = trainingAssignmentTestSetRepository;
		this.testAttemptRepository = testAttemptRepository;
		this.userResponseRepository = userResponseRepository;
		this.testQuestionRepository = testQuestionRepository;
		this.questionOptionRepository = questionOptionRepository;
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResultByTrainingAssignmentId(Long trainingAssignmentId) {

		try {

			// =====================================================
			// VALIDATION
			// =====================================================

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity(
						"Invalid training assignment id",
						HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Optional<TrainingAssignment> optionalAssignment =
					trainingAssignmentRepository.findById(trainingAssignmentId);

			if (optionalAssignment.isEmpty()) {

				return new ResponseEntity(
						"Training assignment not found with id: " + trainingAssignmentId,
						HttpStatus.NOT_FOUND.value(),
						null);
			}

			TrainingAssignment assignment = optionalAssignment.get();

			// =====================================================
			// MAIN RESPONSE
			// =====================================================

			TrainingResultResponse response = new TrainingResultResponse();

			response.setTrainingAssignmentId(
					assignment.getTrainingAssignmentId());

			response.setUserId(
					assignment.getUserId());

			if (assignment.getTraining() != null) {

				response.setTrainingId(
						assignment.getTraining().getTrainingId());

				response.setTrainingName(
						assignment.getTraining().getTrainingName());
			}

			// =====================================================
			// 1. IN-TRAINING QUESTIONS
			// =====================================================

			List<InTrainingUserResponse> inTrainingResponses =
					inTrainingUserResponseRepository
							.findByTrainingAssignment_TrainingAssignmentId(
									trainingAssignmentId);

			InTrainingResultResponse inTrainingResult =
					new InTrainingResultResponse();

			List<InTrainingQuestionResultResponse> questionResults =
					new ArrayList<>();

			int totalQuestions = 0;
			int attemptedQuestions = 0;
			int correctAnswers = 0;
			int wrongAnswers = 0;
			int skippedQuestions = 0;

			if (inTrainingResponses != null
					&& !inTrainingResponses.isEmpty()) {

				totalQuestions = inTrainingResponses.size();

				for (InTrainingUserResponse userResponse :
						inTrainingResponses) {

					InTrainingQuestionResultResponse questionResult =
							new InTrainingQuestionResultResponse();

					// ---------------------------------------------
					// Question details
					// ---------------------------------------------

					InTrainingQuestion question =
							userResponse.getQuestion();

					if (question != null) {

						questionResult.setQuestionId(
								question.getQuestionId());

						if (question.getVideo() != null) {

							questionResult.setVideoId(
									question.getVideo().getVideoId());
						}

						questionResult.setQuestion(
								question.getQuestionText());

						if (question.getQuestionType() != null) {

							questionResult.setQuestionType(
									question.getQuestionType().name());
						}

						questionResult.setCorrectAnswer(
								question.getCorrectAnswer());
					}

					// ---------------------------------------------
					// User response
					// ---------------------------------------------

					questionResult.setUserAnswer(
							userResponse.getUserAnswer());

					questionResult.setIsCorrect(
							userResponse.getIsCorrect());

					questionResult.setIsSkipped(
							userResponse.getIsSkipped());

					questionResult.setTimeTakenSeconds(
							userResponse.getTimeTakenSeconds());

					questionResults.add(questionResult);

					// ---------------------------------------------
					// Calculate summary
					// ---------------------------------------------

					if (Boolean.TRUE.equals(
							userResponse.getIsSkipped())) {

						skippedQuestions++;

					} else {

						attemptedQuestions++;

						if (Boolean.TRUE.equals(
								userResponse.getIsCorrect())) {

							correctAnswers++;

						} else {

							wrongAnswers++;
						}
					}
				}
			}

			// =====================================================
			// IN-TRAINING SUMMARY
			// =====================================================

			inTrainingResult.setTrainingAssignmentId(
					trainingAssignmentId);

			inTrainingResult.setTrainingId(
					response.getTrainingId());

			inTrainingResult.setTrainingName(
					response.getTrainingName());

			inTrainingResult.setTotalQuestions(
					totalQuestions);

			inTrainingResult.setAttemptedQuestions(
					attemptedQuestions);

			inTrainingResult.setCorrectAnswers(
					correctAnswers);

			inTrainingResult.setWrongAnswers(
					wrongAnswers);

			inTrainingResult.setSkippedQuestions(
					skippedQuestions);

			// =====================================================
			// CALCULATE IN-TRAINING SCORE
			// =====================================================

			if (totalQuestions > 0) {

				BigDecimal overallScore =
						BigDecimal.valueOf(correctAnswers)
								.multiply(BigDecimal.valueOf(100))
								.divide(
										BigDecimal.valueOf(totalQuestions),
										2,
										RoundingMode.HALF_UP);

				inTrainingResult.setOverallScore(
						overallScore);

				BigDecimal passingPercentage = null;

				if (assignment.getTraining() != null) {

					passingPercentage =
							assignment.getTraining()
									.getPassingPercentage();
				}

				inTrainingResult.setPassingPercentage(
						passingPercentage);

				if (passingPercentage != null) {

					boolean passed =
							overallScore.compareTo(
									passingPercentage) >= 0;

					inTrainingResult.setPassed(
							passed);

					inTrainingResult.setResult(
							passed ? "PASS" : "FAIL");
				}
			}

			inTrainingResult.setQuestions(
					questionResults);

			response.setInTrainingResult(
					inTrainingResult);

			// =====================================================
			// 2. TEST SET
			// =====================================================

			Optional<TrainingAssignmentTestSet> optionalAssignmentTestSet =
					trainingAssignmentTestSetRepository
							.findByTrainingAssignment_TrainingAssignmentId(
									trainingAssignmentId);

			if (optionalAssignmentTestSet.isPresent()) {

				TrainingAssignmentTestSet assignmentTestSet =
						optionalAssignmentTestSet.get();

				TestSet testSet =
						assignmentTestSet.getTestSet();

				if (testSet != null) {

					TestSetResultResponse testSetResult =
							new TestSetResultResponse();

					testSetResult.setTestSetId(
							testSet.getTestSetId());

					testSetResult.setTestName(
							testSet.getTestName());

					testSetResult.setPassingPercentage(
							testSet.getPassingPercentage());

					// ---------------------------------------------
					// Find user's latest attempt
					// ---------------------------------------------

					Optional<TestAttempt> optionalAttempt =
							testAttemptRepository
									.findTopByTrainingAssignment_TrainingAssignmentIdOrderByTestAttemptIdDesc(
											trainingAssignmentId);

					if (optionalAttempt.isPresent()) {

						TestAttempt attempt =
								optionalAttempt.get();

						testSetResult.setTestAttemptId(
								attempt.getTestAttemptId());

						if (attempt.getResult() != null) {

							testSetResult.setResult(
									attempt.getResult().name());
						}

						if (attempt.getStatus() != null) {

							testSetResult.setStatus(
									attempt.getStatus().name());
						}

						testSetResult.setTotalScore(
								attempt.getTotalScore());

						testSetResult.setTotalQuestions(
								attempt.getTotalQuestions());

						testSetResult.setCorrectAnswers(
								attempt.getCorrectAnswers());

						testSetResult.setWrongAnswers(
								attempt.getWrongAnswers());

						testSetResult.setSkippedQuestions(
								attempt.getSkippedQuestions());

						testSetResult.setPassingPercentage(
								attempt.getPassingPercentage());

						// -----------------------------------------
						// Get user's answers
						// -----------------------------------------

						List<UserResponse> userResponses =
								userResponseRepository
										.findByTestAttempt_TestAttemptId(
												attempt.getTestAttemptId());

						Map<Long, UserResponse> responseMap =
								userResponses.stream()
										.filter(r -> r.getTestQuestion() != null)
										.collect(Collectors.toMap(
												r -> r.getTestQuestion()
														.getTestQuestionId(),
												r -> r,
												(first, second) -> second));

						// -----------------------------------------
						// Get test questions
						// -----------------------------------------

						List<TestQuestion> questions =
								testQuestionRepository
										.findByTestSet_TestSetIdOrderByDisplayOrderAsc(
												testSet.getTestSetId());

						List<TestQuestionResultResponse> questionResultsTest =
								new ArrayList<>();

						for (TestQuestion question : questions) {

							TestQuestionResultResponse questionResult =
									new TestQuestionResultResponse();

							questionResult.setTestQuestionId(
									question.getTestQuestionId());

							questionResult.setQuestion(
									question.getQuestionText());

							if (question.getQuestionType() != null) {

								questionResult.setQuestionType(
										question.getQuestionType().name());
							}

							questionResult.setCorrectAnswer(
									question.getCorrectAnswer());

							// -------------------------------------
							// Options
							// -------------------------------------

							List<QuestionOption> options =
									questionOptionRepository
											.findByTestQuestion_TestQuestionIdOrderByDisplayOrderAsc(
													question.getTestQuestionId());

							List<TestQuestionOptionResponse> optionResponses =
									new ArrayList<>();

							for (QuestionOption option : options) {

								TestQuestionOptionResponse optionResponse =
										new TestQuestionOptionResponse();

								optionResponse.setOptionId(
										option.getQuestionOptionId());

								optionResponse.setOptionText(
										option.getOptionText());

								optionResponse.setOptionLabel(
										option.getOptionLabel());

								optionResponse.setIsCorrect(
										option.getIsCorrect());

								optionResponses.add(
										optionResponse);
							}

							questionResult.setOptions(
									optionResponses);

							// -------------------------------------
							// User answer
							// -------------------------------------

							UserResponse userResponse =
									responseMap.get(
											question.getTestQuestionId());

							if (userResponse != null) {

								questionResult.setUserAnswer(
										userResponse.getUserAnswer());

								questionResult.setIsCorrect(
										userResponse.getIsCorrect());
							}

							questionResultsTest.add(
									questionResult);
						}

						testSetResult.setQuestions(
								questionResultsTest);
					}

					response.setTestSet(
							testSetResult);
				}
			}

			// =====================================================
			// SUCCESS
			// =====================================================

			return new ResponseEntity(
					"Training result fetched successfully",
					HttpStatus.OK.value(),
					response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity(
					"Internal server error!",
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					null);
		}
	}
}