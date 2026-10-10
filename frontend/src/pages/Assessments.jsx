import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authFetch, clearAuth, getUser } from "../api";
import "./Assessments.css";

function Assessments() {
  const [user, setUser] = useState(null);
  const [assessments, setAssessments] = useState([]);
  const [userHistory, setUserHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  // Active Quiz State
  const [activeQuiz, setActiveQuiz] = useState(null);
  const [currentQuestionIdx, setCurrentQuestionIdx] = useState(0);
  const [userAnswers, setUserAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(600); // 10 minutes in seconds
  const [submitting, setSubmitting] = useState(false);
  const [quizResult, setQuizResult] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = getUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }

    setUser(currentUser);
    loadAssessmentsAndHistory(currentUser.id);
  }, [navigate]);

  // Quiz Countdown Timer
  useEffect(() => {
    if (!activeQuiz || quizResult) return;

    if (timeLeft <= 0) {
      handleAutoSubmit();
      return;
    }

    const timer = setInterval(() => {
      setTimeLeft((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [activeQuiz, quizResult, timeLeft]);

  const loadAssessmentsAndHistory = async (userId) => {
    setLoading(true);
    try {
      const [asmRes, histRes] = await Promise.all([
        authFetch("/assessments"),
        authFetch(`/assessments/results/${userId}`),
      ]);

      if (asmRes.ok) {
        const data = await asmRes.json();
        setAssessments(data);
      }
      if (histRes.ok) {
        const data = await histRes.json();
        setUserHistory(data);
      }
    } catch (err) {
      console.error("Failed to load assessments:", err);
    } finally {
      setLoading(false);
    }
  };

  const startAssessment = (assessment) => {
    setActiveQuiz(assessment);
    setCurrentQuestionIdx(0);
    setUserAnswers({});
    setTimeLeft(assessment.timeLimitMinutes * 60);
    setQuizResult(null);
  };

  const handleSelectOption = (questionId, optionIdx) => {
    setUserAnswers((prev) => ({
      ...prev,
      [questionId]: optionIdx,
    }));
  };

  const handleAutoSubmit = () => {
    alert("Time is up! Submitting your answers now.");
    handleSubmitQuiz();
  };

  const handleSubmitQuiz = async () => {
    if (!user || !activeQuiz) return;
    setSubmitting(true);

    const payload = {
      assessmentId: activeQuiz.id,
      answers: userAnswers,
    };

    try {
      const res = await authFetch(`/assessments/submit/${user.id}`, {
        method: "POST",
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        const result = await res.json();
        setQuizResult(result);
        // Refresh past attempts history
        loadAssessmentsAndHistory(user.id);
      } else {
        alert("Failed to submit assessment. Please try again.");
      }
    } catch (err) {
      console.error("Error submitting assessment:", err);
      alert("Network error while submitting assessment.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  if (!user || loading) {
    return (
      <div className="assessments-loading">
        <div className="spinner"></div>
        <p>Loading SkillSphere Assessment Center...</p>
      </div>
    );
  }

  // Format MM:SS for countdown timer
  const minutes = Math.floor(timeLeft / 60);
  const seconds = timeLeft % 60;
  const timeFormatted = `${minutes.toString().padStart(2, "0")}:${seconds
    .toString()
    .padStart(2, "0")}`;

  return (
    <div className="assessments-page">
      {/* Top Navbar */}
      <header className="assessments-header">
        <div className="header-brand">
          <Link to="/dashboard" className="brand-logo">
            SkillSphere <span>AI</span>
          </Link>
          <span className="brand-divider">/</span>
          <span className="brand-crumb">Assessments & Certification</span>
        </div>

        <div className="header-actions">
          <Link to="/roadmap" className="nav-link">
            🗺️ Roadmap
          </Link>
          <Link to="/courses" className="nav-link">
            📚 Courses
          </Link>
          <Link to="/dashboard" className="nav-link">
            ⬅️ Dashboard
          </Link>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="assessments-content">
        {/* ======================================================== */}
        {/* 1. QUIZ RESULT SCREEN */}
        {/* ======================================================== */}
        {quizResult ? (
          <div className="quiz-result-view">
            <div
              className={`result-hero-card ${
                quizResult.passed ? "passed" : "failed"
              }`}
            >
              <div className="result-icon">
                {quizResult.passed ? "🏆" : "📖"}
              </div>
              <h2>
                {quizResult.passed
                  ? "Assessment Passed!"
                  : "Keep Learning & Retake"}
              </h2>
              <div className="result-score-badge">
                Score: {quizResult.score} / {quizResult.totalQuestions} (
                {quizResult.percentage}%)
              </div>
              <div className="result-badge-awarded">
                Badge Awarded: <strong>{quizResult.badgeAwarded}</strong>
              </div>
              <p className="result-feedback">{quizResult.feedback}</p>

              {quizResult.passed && (
                <div className="verified-success-callout">
                  ✨ <strong>{quizResult.skill}</strong> has been verified and
                  automatically added to your profile skillset!
                </div>
              )}

              <div className="result-actions">
                <button
                  className="retry-btn"
                  onClick={() => startAssessment(activeQuiz)}
                >
                  🔄 Retake Assessment
                </button>
                <button
                  className="back-btn"
                  onClick={() => {
                    setActiveQuiz(null);
                    setQuizResult(null);
                  }}
                >
                  ⬅️ Back to All Assessments
                </button>
                <Link to="/skill-gap" className="view-gap-btn">
                  Check Updated Skill Gap ➔
                </Link>
              </div>
            </div>

            {/* Answer-by-Answer Review */}
            <div className="review-section">
              <h3>Detailed Question Review & Explanations</h3>
              <div className="review-list">
                {quizResult.reviews?.map((q, idx) => (
                  <div
                    key={q.questionId}
                    className={`review-card ${
                      q.isCorrect ? "correct" : "incorrect"
                    }`}
                  >
                    <div className="review-card-header">
                      <span className="q-badge">Question {idx + 1}</span>
                      <span
                        className={`correct-status ${
                          q.isCorrect ? "correct" : "incorrect"
                        }`}
                      >
                        {q.isCorrect ? "✓ Correct (+1)" : "✗ Incorrect (0)"}
                      </span>
                    </div>

                    <h4>{q.question}</h4>

                    <div className="options-review">
                      {q.options?.map((opt, optIdx) => {
                        const isChosen = q.selectedOption === optIdx;
                        const isActualCorrect = q.correctOption === optIdx;
                        let optionClass = "";
                        if (isActualCorrect) optionClass = "is-correct";
                        else if (isChosen && !isActualCorrect)
                          optionClass = "is-wrong";

                        return (
                          <div
                            key={optIdx}
                            className={`review-option ${optionClass}`}
                          >
                            <span>
                              {optIdx + 1}. {opt}
                            </span>
                            {isActualCorrect && (
                              <span className="opt-tag correct">
                                Correct Answer
                              </span>
                            )}
                            {isChosen && !isActualCorrect && (
                              <span className="opt-tag wrong">Your Answer</span>
                            )}
                          </div>
                        );
                      })}
                    </div>

                    <div className="explanation-box">
                      <strong>💡 Explanation:</strong>
                      <p>{q.explanation}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ) : activeQuiz ? (
          /* ======================================================== */
          /* 2. ACTIVE QUIZ TEST RUNNER SCREEN */
          /* ======================================================== */
          <div className="quiz-runner-view">
            <div className="quiz-header-bar">
              <div>
                <h2>{activeQuiz.title}</h2>
                <span className="quiz-skill-tag">Skill: {activeQuiz.skill}</span>
              </div>
              <div className="timer-badge">
                ⏳ Time Remaining: <strong>{timeFormatted}</strong>
              </div>
            </div>

            {/* Question Progress bar */}
            <div className="question-progress-bar">
              <div
                className="q-progress-fill"
                style={{
                  width: `${
                    ((currentQuestionIdx + 1) /
                      activeQuiz.questions.length) *
                    100
                  }%`,
                }}
              ></div>
            </div>

            {/* Active Question Card */}
            {activeQuiz.questions && (
              <div className="active-question-card">
                <div className="card-top-meta">
                  <span className="q-number-pill">
                    Question {currentQuestionIdx + 1} of{" "}
                    {activeQuiz.questions.length}
                  </span>
                  <span className="q-point-pill">1 Point</span>
                </div>

                <h3>{activeQuiz.questions[currentQuestionIdx].question}</h3>

                <div className="options-container">
                  {activeQuiz.questions[currentQuestionIdx].options.map(
                    (opt, optIdx) => {
                      const qId =
                        activeQuiz.questions[currentQuestionIdx].id;
                      const isSelected = userAnswers[qId] === optIdx;

                      return (
                        <div
                          key={optIdx}
                          className={`option-row ${
                            isSelected ? "selected" : ""
                          }`}
                          onClick={() => handleSelectOption(qId, optIdx)}
                        >
                          <div className="radio-circle">
                            {isSelected && <div className="radio-dot"></div>}
                          </div>
                          <span className="option-text">{opt}</span>
                        </div>
                      );
                    }
                  )}
                </div>

                {/* Question Navigation Footer */}
                <div className="question-nav-footer">
                  <button
                    className="nav-prev-btn"
                    disabled={currentQuestionIdx === 0}
                    onClick={() =>
                      setCurrentQuestionIdx((prev) => Math.max(0, prev - 1))
                    }
                  >
                    ⬅️ Previous
                  </button>

                  <div className="question-dots">
                    {activeQuiz.questions.map((q, idx) => (
                      <span
                        key={q.id}
                        className={`q-dot ${
                          userAnswers[q.id] !== undefined ? "answered" : ""
                        } ${currentQuestionIdx === idx ? "current" : ""}`}
                        onClick={() => setCurrentQuestionIdx(idx)}
                      >
                        {idx + 1}
                      </span>
                    ))}
                  </div>

                  {currentQuestionIdx < activeQuiz.questions.length - 1 ? (
                    <button
                      className="nav-next-btn"
                      onClick={() =>
                        setCurrentQuestionIdx((prev) =>
                          Math.min(activeQuiz.questions.length - 1, prev + 1)
                        )
                      }
                    >
                      Next ➡️
                    </button>
                  ) : (
                    <button
                      className="submit-quiz-btn"
                      disabled={submitting}
                      onClick={handleSubmitQuiz}
                    >
                      {submitting ? "Grading..." : "Submit Assessment ✓"}
                    </button>
                  )}
                </div>
              </div>
            )}
          </div>
        ) : (
          /* ======================================================== */
          /* 3. ASSESSMENTS CATALOG & PAST HISTORY SCREEN */
          /* ======================================================== */
          <div>
            <div className="assessments-title-bar">
              <div>
                <h1>Skill Verification & Assessments 📝</h1>
                <p>
                  Test your technical proficiency. Passing scores (≥ 60%)
                  automatically certify your skills and boost your Job
                  Readiness Score.
                </p>
              </div>
            </div>

            {/* User Badges & Past History summary */}
            {userHistory.length > 0 && (
              <div className="history-summary-card">
                <h3>🏆 Your Verified Certifications & Past Attempts</h3>
                <div className="history-table-container">
                  <table className="history-table">
                    <thead>
                      <tr>
                        <th>Assessment</th>
                        <th>Skill</th>
                        <th>Score</th>
                        <th>Status</th>
                        <th>Date</th>
                      </tr>
                    </thead>
                    <tbody>
                      {userHistory.slice(0, 5).map((h) => (
                        <tr key={h.id}>
                          <td>{h.assessmentTitle}</td>
                          <td>
                            <span className="history-skill-tag">{h.skill}</span>
                          </td>
                          <td>
                            <strong>
                              {h.score} / {h.totalQuestions} ({h.percentage}%)
                            </strong>
                          </td>
                          <td>
                            <span
                              className={`history-status-tag ${
                                h.passed ? "passed" : "failed"
                              }`}
                            >
                              {h.passed ? "✓ Passed" : "✗ Needs Practice"}
                            </span>
                          </td>
                          <td>
                            {new Date(h.completedAt).toLocaleDateString()}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Assessment Cards Grid */}
            <h3 className="section-subtitle">Available Skill Quizzes</h3>
            <div className="assessments-grid">
              {assessments.map((asm) => (
                <div key={asm.id} className="assessment-card">
                  <div className="asm-top">
                    <span className="asm-icon">{asm.icon}</span>
                    <span className="asm-diff-badge">{asm.difficulty}</span>
                  </div>

                  <span className="asm-skill-pill">Skill: {asm.skill}</span>
                  <h3>{asm.title}</h3>

                  <div className="asm-meta-row">
                    <span>❓ {asm.questions?.length || 5} Questions</span>
                    <span>⏱️ {asm.timeLimitMinutes} Minutes</span>
                    <span>🎯 60% Passing Grade</span>
                  </div>

                  <button
                    className="start-quiz-btn"
                    onClick={() => startAssessment(asm)}
                  >
                    Take Assessment ➔
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default Assessments;
