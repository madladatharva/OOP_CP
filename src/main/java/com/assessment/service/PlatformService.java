package com.assessment.service;

import com.assessment.dao.PlatformDAO;
import com.assessment.dao.UserDAO;
import com.assessment.model.AssessmentSession;
import com.assessment.model.Classroom;
import com.assessment.model.Question;
import com.assessment.model.QuizAssignment;
import com.assessment.model.SessionQuestion;
import com.assessment.model.Subject;
import com.assessment.model.TeacherQuiz;
import com.assessment.model.Topic;
import com.assessment.model.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PlatformService {

    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";

    private final PlatformDAO platformDAO;
    private final UserDAO userDAO;

    public PlatformService() {
        this.platformDAO = new PlatformDAO();
        this.userDAO = new UserDAO();
    }

    public List<Subject> getSubjects() throws SQLException {
        return platformDAO.listSubjects();
    }

    public List<Topic> getTopics(Integer subjectId) throws SQLException {
        return platformDAO.listTopics(subjectId);
    }

    public Map<String, Object> getTeacherDashboardData(int teacherId) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        List<Classroom> classrooms = platformDAO.listTeacherClassrooms(teacherId);
        List<Question> questions = platformDAO.listQuestions(null, null, null);
        List<TeacherQuiz> quizzes = platformDAO.listTeacherQuizzes(teacherId);
        List<QuizAssignment> assignments = platformDAO.listTeacherAssignments(teacherId);
        List<Map<String, Object>> weakTopics = platformDAO.getTeacherWeakTopics(teacherId);

        long dueSoon = assignments.stream()
                .filter(a -> a.getDeadlineAt() != null && a.getDeadlineAt().toInstant().isBefore(Instant.now().plus(3, ChronoUnit.DAYS)))
                .count();

        data.put("classrooms", classrooms);
        data.put("quizzes", quizzes);
        data.put("assignments", assignments);
        data.put("weakTopics", weakTopics);
        data.put("classroomCount", classrooms.size());
        data.put("questionCount", questions.size());
        data.put("quizCount", quizzes.size());
        data.put("dueSoonCount", dueSoon);
        return data;
    }

    public List<Classroom> getTeacherClassrooms(int teacherId) throws SQLException {
        return platformDAO.listTeacherClassrooms(teacherId);
    }

    public List<Classroom> getStudentClassrooms(int studentId) throws SQLException {
        return platformDAO.listStudentClassrooms(studentId);
    }

    public Classroom createClassroom(int teacherId, String name, String description) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name is required.");
        }

        String code;
        do {
            code = generateClassCode();
        } while (platformDAO.findClassroomByCode(code) != null);

        int id = platformDAO.createClassroom(teacherId, name.trim(), code, normalizeOptional(description));
        return platformDAO.findTeacherClassroom(teacherId, id);
    }

    public Classroom getTeacherClassroom(int teacherId, int classroomId) throws SQLException {
        return platformDAO.findTeacherClassroom(teacherId, classroomId);
    }

    public List<User> getClassroomStudents(int classroomId) throws SQLException {
        return platformDAO.listClassroomStudents(classroomId);
    }

    public void addStudentToClassroom(int teacherId, int classroomId, String identifier) throws SQLException {
        Classroom classroom = platformDAO.findTeacherClassroom(teacherId, classroomId);
        if (classroom == null) {
            throw new IllegalArgumentException("Classroom not found.");
        }
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Student username or email is required.");
        }

        User student = platformDAO.findStudentByIdentifier(identifier.trim());
        if (student == null) {
            throw new IllegalArgumentException("Student account not found.");
        }
        if (platformDAO.isStudentEnrolled(classroomId, student.getId())) {
            throw new IllegalArgumentException("Student is already enrolled in this class.");
        }

        platformDAO.enrollStudent(classroomId, student.getId(), "MANUAL");
    }

    public Classroom joinClassroom(int studentId, String classCode) throws SQLException {
        if (classCode == null || classCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Class code is required.");
        }

        Classroom classroom = platformDAO.findClassroomByCode(classCode.trim().toUpperCase(Locale.ROOT));
        if (classroom == null) {
            throw new IllegalArgumentException("Class code not found.");
        }
        if (!platformDAO.isStudentEnrolled(classroom.getId(), studentId)) {
            platformDAO.enrollStudent(classroom.getId(), studentId, "CODE");
        }
        return classroom;
    }

    public List<Question> searchQuestionBank(Integer subjectId, Integer topicId, Integer difficulty) throws SQLException {
        return platformDAO.listQuestions(subjectId, topicId, difficulty);
    }

    public Question getQuestion(int questionId) throws SQLException {
        return platformDAO.findQuestionById(questionId);
    }

    public Question saveQuestion(int teacherId, Integer questionId, int subjectId, int topicId, String questionText,
            String optionA, String optionB, String optionC, String optionD, String correctOption, int difficultyLevel)
            throws SQLException {
        validateQuestion(subjectId, topicId, questionText, optionA, optionB, optionC, optionD, correctOption, difficultyLevel);
        Topic topic = platformDAO.findTopicById(topicId);
        if (topic == null || topic.getSubjectId() != subjectId) {
            throw new IllegalArgumentException("Selected topic does not belong to the chosen subject.");
        }

        Question question = new Question();
        question.setId(questionId == null ? 0 : questionId);
        question.setSubjectId(subjectId);
        question.setTopicId(topicId);
        question.setQuestionText(questionText.trim());
        question.setOptionA(optionA.trim());
        question.setOptionB(optionB.trim());
        question.setOptionC(optionC.trim());
        question.setOptionD(optionD.trim());
        question.setCorrectOption(correctOption.trim().toUpperCase(Locale.ROOT));
        question.setDifficultyLevel(difficultyLevel);
        question.setCategory(topic.getName());
        question.setCreatedBy(teacherId);

        if (questionId == null) {
            int newId = platformDAO.insertQuestion(question);
            return platformDAO.findQuestionById(newId);
        }

        platformDAO.updateQuestion(question);
        return platformDAO.findQuestionById(questionId);
    }

    public void archiveQuestion(int questionId) throws SQLException {
        platformDAO.archiveQuestion(questionId);
    }

    public int importQuestionsFromCsv(int teacherId, Part csvFile) throws SQLException, IOException {
        if (csvFile == null || csvFile.getSize() == 0) {
            throw new IllegalArgumentException("Please upload a CSV file.");
        }

        int inserted = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8));
                CSVParser parser = CSVParser.parse(reader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreEmptyLines().withTrim())) {
            for (CSVRecord record : parser) {
                String subjectValue = record.get("subject");
                String topicValue = record.get("topic");
                String difficultyValue = record.get("difficulty");
                String questionText = record.get("question_text");
                String optionA = record.get("option_a");
                String optionB = record.get("option_b");
                String optionC = record.get("option_c");
                String optionD = record.get("option_d");
                String correctOption = record.get("correct_option");

                Subject subject = platformDAO.findSubjectByCodeOrName(subjectValue.trim());
                if (subject == null) {
                    throw new IllegalArgumentException("Unknown subject in CSV: " + subjectValue);
                }
                int topicId = platformDAO.findOrCreateTopic(subject.getId(), topicValue.trim());
                int difficulty = normalizeDifficulty(difficultyValue);

                saveQuestion(teacherId, null, subject.getId(), topicId, questionText, optionA, optionB, optionC,
                        optionD, correctOption, difficulty);
                inserted++;
            }
        }
        return inserted;
    }

    public List<TeacherQuiz> getTeacherQuizzes(int teacherId) throws SQLException {
        return platformDAO.listTeacherQuizzes(teacherId);
    }

    public List<QuizAssignment> getTeacherAssignments(int teacherId) throws SQLException {
        return platformDAO.listTeacherAssignments(teacherId);
    }

    public TeacherQuiz createQuiz(int teacherId, int subjectId, String title, String description, List<Integer> topicIds,
            int questionCount, int timeLimitMinutes) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title is required.");
        }
        if (topicIds == null || topicIds.isEmpty()) {
            throw new IllegalArgumentException("Select at least one topic.");
        }
        if (questionCount < 3 || questionCount > 30) {
            throw new IllegalArgumentException("Question count must be between 3 and 30.");
        }
        if (timeLimitMinutes < 5 || timeLimitMinutes > 120) {
            throw new IllegalArgumentException("Time limit must be between 5 and 120 minutes.");
        }
        for (Integer topicId : topicIds) {
            Topic topic = platformDAO.findTopicById(topicId);
            if (topic == null || topic.getSubjectId() != subjectId) {
                throw new IllegalArgumentException("Every selected topic must belong to the chosen subject.");
            }
        }

        int availableQuestions = platformDAO.countActiveQuestions(subjectId, topicIds);
        if (availableQuestions < questionCount) {
            throw new IllegalArgumentException("Only " + availableQuestions + " tagged questions are available for the selected subject/topics.");
        }

        TeacherQuiz quiz = new TeacherQuiz();
        quiz.setTeacherId(teacherId);
        quiz.setSubjectId(subjectId);
        quiz.setTitle(title.trim());
        quiz.setDescription(normalizeOptional(description));
        quiz.setQuestionCount(questionCount);
        quiz.setTimeLimitMinutes(timeLimitMinutes);
        quiz.setStartDifficulty(2);

        int quizId = platformDAO.createQuiz(quiz);
        platformDAO.replaceQuizTopics(quizId, topicIds);
        return platformDAO.findTeacherQuiz(teacherId, quizId);
    }

    public void assignQuiz(int teacherId, int quizId, int classroomId, Timestamp deadlineAt) throws SQLException {
        TeacherQuiz quiz = platformDAO.findTeacherQuiz(teacherId, quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found.");
        }
        Classroom classroom = platformDAO.findTeacherClassroom(teacherId, classroomId);
        if (classroom == null) {
            throw new IllegalArgumentException("Classroom not found.");
        }
        if (deadlineAt == null || deadlineAt.toInstant().isBefore(Instant.now().plus(5, ChronoUnit.MINUTES))) {
            throw new IllegalArgumentException("Deadline must be at least 5 minutes in the future.");
        }
        platformDAO.createAssignment(quizId, classroomId, deadlineAt);
    }

    public Map<String, List<QuizAssignment>> getStudentAssignmentBuckets(int studentId) throws SQLException {
        List<QuizAssignment> assignments = platformDAO.listStudentAssignments(studentId);
        List<QuizAssignment> assigned = new ArrayList<>();
        List<QuizAssignment> completed = new ArrayList<>();
        List<QuizAssignment> missed = new ArrayList<>();

        Instant now = Instant.now();
        for (QuizAssignment assignment : assignments) {
            boolean completedState = isCompletedStatus(assignment.getSessionStatus());
            boolean missedState = !completedState && assignment.getDeadlineAt() != null
                    && assignment.getDeadlineAt().toInstant().isBefore(now);
            if (completedState) {
                completed.add(assignment);
            } else if (missedState) {
                missed.add(assignment);
            } else {
                assigned.add(assignment);
            }
        }

        Map<String, List<QuizAssignment>> buckets = new LinkedHashMap<>();
        buckets.put("assigned", assigned);
        buckets.put("completed", completed);
        buckets.put("missed", missed);
        return buckets;
    }

    public Map<String, Object> getStudentDashboardData(int studentId) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("classes", platformDAO.listStudentClassrooms(studentId));
        data.put("assignments", getStudentAssignmentBuckets(studentId));
        data.put("subjectPerformance", platformDAO.getStudentSubjectPerformance(studentId));
        data.put("topicPerformance", platformDAO.getStudentTopicPerformance(studentId));
        data.put("recentResults", platformDAO.getStudentRecentResults(studentId));
        return data;
    }

    public AssessmentSession ensureStudentSession(int studentId, int assignmentId) throws SQLException {
        QuizAssignment assignment = platformDAO.findStudentAssignment(assignmentId, studentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not available to this student.");
        }
        if (assignment.getDeadlineAt() != null && assignment.getDeadlineAt().toInstant().isBefore(Instant.now())
                && !isCompletedStatus(assignment.getSessionStatus())) {
            throw new IllegalArgumentException("This quiz assignment has passed its deadline.");
        }

        AssessmentSession session = platformDAO.findAssessmentSessionForAssignment(assignmentId, studentId);
        if (session == null) {
            session = new AssessmentSession();
            session.setAssignmentId(assignmentId);
            session.setStudentId(studentId);
            session.setQuizId(assignment.getQuizId());
            session.setCurrentQuestionNumber(0);
            session.setTotalQuestions(assignment.getQuestionCount());
            session.setCurrentDifficulty(2);
            session.setScore(0);
            session.setStatus("IN_PROGRESS");
            session.setTimeLimitMinutes(assignment.getTimeLimitMinutes());
            session.setStartedAt(Timestamp.from(Instant.now()));
            session.setLastActivityAt(Timestamp.from(Instant.now()));
            session.setId(platformDAO.createAssessmentSession(session));
            session = platformDAO.findAssessmentSession(session.getId(), studentId);
        }

        if ("IN_PROGRESS".equals(session.getStatus()) && getRemainingSeconds(session) <= 0) {
            autoSubmitSession(session);
            session = platformDAO.findAssessmentSession(session.getId(), studentId);
        }
        return session;
    }

    public SessionQuestion getCurrentQuestion(int studentId, int assignmentId) throws SQLException {
        AssessmentSession session = ensureStudentSession(studentId, assignmentId);
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            return null;
        }

        if (session.getCurrentQuestionNumber() >= session.getTotalQuestions()) {
            autoSubmitSession(session);
            return null;
        }

        SessionQuestion existing = platformDAO.findUnansweredSessionQuestion(session.getId());
        if (existing != null) {
            return existing;
        }

        TeacherQuiz quiz = platformDAO.findQuizById(session.getQuizId());
        if (quiz == null) {
            throw new IllegalStateException("Quiz configuration not found.");
        }

        List<Integer> topicIds = platformDAO.listQuizTopicIds(quiz.getId());
        Map<Integer, Integer> servedCounts = platformDAO.countServedQuestionsByTopic(session.getId());
        List<Integer> excludedIds = platformDAO.listServedQuestionIds(session.getId());

        Map<Integer, Integer> topicQuotas = buildTopicQuotas(topicIds, session.getTotalQuestions());
        List<Integer> orderedTopics = new ArrayList<>(topicIds);
        orderedTopics.sort(Comparator.<Integer>comparingInt(
                topicId -> topicQuotas.getOrDefault(topicId, 0) - servedCounts.getOrDefault(topicId, 0)).reversed()
                .thenComparingInt(topicId -> servedCounts.getOrDefault(topicId, 0)));

        Question chosen = null;
        for (Integer topicId : orderedTopics) {
            if (servedCounts.getOrDefault(topicId, 0) >= topicQuotas.getOrDefault(topicId, 0)) {
                continue;
            }
            chosen = platformDAO.findAdaptiveQuestionCandidate(quiz.getSubjectId(), topicId, excludedIds,
                    session.getCurrentDifficulty());
            if (chosen != null) {
                break;
            }
        }

        if (chosen == null) {
            for (Integer topicId : orderedTopics) {
                chosen = platformDAO.findAdaptiveQuestionCandidate(quiz.getSubjectId(), topicId, excludedIds,
                        session.getCurrentDifficulty());
                if (chosen != null) {
                    break;
                }
            }
        }

        if (chosen == null) {
            autoSubmitSession(session);
            return null;
        }

        int newId = platformDAO.insertSessionQuestion(
                session.getId(),
                chosen,
                session.getCurrentQuestionNumber() + 1,
                chosen.getDifficultyLevel());
        return platformDAO.findSessionQuestion(newId, studentId);
    }

    public AssessmentSession submitAnswer(int studentId, int sessionId, int sessionQuestionId, String selectedOption)
            throws SQLException {
        AssessmentSession session = platformDAO.findAssessmentSession(sessionId, studentId);
        if (session == null) {
            throw new IllegalArgumentException("Quiz session not found.");
        }
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            return session;
        }
        if (getRemainingSeconds(session) <= 0) {
            autoSubmitSession(session);
            return platformDAO.findAssessmentSession(sessionId, studentId);
        }

        SessionQuestion sessionQuestion = platformDAO.findSessionQuestion(sessionQuestionId, studentId);
        if (sessionQuestion == null || sessionQuestion.getSessionId() != sessionId) {
            throw new IllegalArgumentException("Question not found for this session.");
        }
        if (sessionQuestion.getSelectedOption() != null) {
            return session;
        }
        if (selectedOption == null || !selectedOption.matches("[A-Da-d]")) {
            throw new IllegalArgumentException("Please select a valid answer.");
        }

        String normalizedOption = selectedOption.toUpperCase(Locale.ROOT);
        boolean correct = sessionQuestion.getCorrectOption().equalsIgnoreCase(normalizedOption);
        Timestamp now = Timestamp.from(Instant.now());

        platformDAO.insertResponse(sessionQuestionId, normalizedOption, correct);
        platformDAO.markSessionQuestionAnswered(sessionQuestionId, now);

        session.setCurrentQuestionNumber(session.getCurrentQuestionNumber() + 1);
        if (correct) {
            session.setScore(session.getScore() + 1);
            session.setCurrentDifficulty(Math.min(3, session.getCurrentDifficulty() + 1));
        } else {
            session.setCurrentDifficulty(Math.max(1, session.getCurrentDifficulty() - 1));
        }
        session.setLastActivityAt(now);

        if (session.getCurrentQuestionNumber() >= session.getTotalQuestions()) {
            session.setStatus("SUBMITTED");
            session.setSubmittedAt(now);
        }

        platformDAO.updateAssessmentSession(session);
        return platformDAO.findAssessmentSession(sessionId, studentId);
    }

    public AssessmentSession submitSessionOnTimeout(int studentId, int sessionId) throws SQLException {
        AssessmentSession session = platformDAO.findAssessmentSession(sessionId, studentId);
        if (session == null) {
            throw new IllegalArgumentException("Quiz session not found.");
        }
        if ("IN_PROGRESS".equals(session.getStatus())) {
            autoSubmitSession(session);
        }
        return platformDAO.findAssessmentSession(sessionId, studentId);
    }

    public AssessmentSession getAssessmentSession(int studentId, int sessionId) throws SQLException {
        return platformDAO.findAssessmentSession(sessionId, studentId);
    }

    public List<SessionQuestion> getSessionResults(int studentId, int sessionId) throws SQLException {
        return platformDAO.listSessionResultQuestions(sessionId, studentId);
    }

    public List<Map<String, Object>> getSessionTopicPerformance(int studentId, int sessionId) throws SQLException {
        return platformDAO.getSessionTopicPerformance(sessionId, studentId);
    }

    public Map<String, Object> getTeacherAnalyticsData(int teacherId) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("classPerformance", platformDAO.getTeacherClassPerformance(teacherId));
        data.put("questionAccuracy", platformDAO.getTeacherQuestionAccuracy(teacherId));
        data.put("weakTopics", platformDAO.getTeacherWeakTopics(teacherId));
        return data;
    }

    public int getRemainingSeconds(AssessmentSession session) {
        long totalSeconds = session.getTimeLimitMinutes() * 60L;
        long elapsedSeconds = ChronoUnit.SECONDS.between(session.getStartedAt().toInstant(), Instant.now());
        return (int) Math.max(0, totalSeconds - elapsedSeconds);
    }

    private void autoSubmitSession(AssessmentSession session) throws SQLException {
        session.setStatus("AUTO_SUBMITTED");
        session.setSubmittedAt(Timestamp.from(Instant.now()));
        session.setLastActivityAt(Timestamp.from(Instant.now()));
        platformDAO.updateAssessmentSession(session);
    }

    private void validateQuestion(int subjectId, int topicId, String questionText, String optionA, String optionB,
            String optionC, String optionD, String correctOption, int difficultyLevel) {
        if (subjectId <= 0 || topicId <= 0) {
            throw new IllegalArgumentException("Subject and topic are required.");
        }
        if (isBlank(questionText) || isBlank(optionA) || isBlank(optionB) || isBlank(optionC) || isBlank(optionD)) {
            throw new IllegalArgumentException("Question text and all four options are required.");
        }
        if (correctOption == null || !correctOption.matches("[A-Da-d]")) {
            throw new IllegalArgumentException("Correct option must be A, B, C, or D.");
        }
        if (difficultyLevel < 1 || difficultyLevel > 3) {
            throw new IllegalArgumentException("Difficulty must be Easy, Medium, or Hard.");
        }
    }

    private String generateClassCode() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    private int normalizeDifficulty(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Difficulty is required.");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "1":
            case "easy":
                return 1;
            case "2":
            case "medium":
                return 2;
            case "3":
            case "hard":
                return 3;
            default:
                throw new IllegalArgumentException("Unsupported difficulty value: " + value);
        }
    }

    private Map<Integer, Integer> buildTopicQuotas(List<Integer> topicIds, int totalQuestions) {
        Map<Integer, Integer> quotas = new LinkedHashMap<>();
        if (topicIds.isEmpty()) {
            return quotas;
        }
        int base = totalQuestions / topicIds.size();
        int remainder = totalQuestions % topicIds.size();
        for (int i = 0; i < topicIds.size(); i++) {
            quotas.put(topicIds.get(i), base + (i < remainder ? 1 : 0));
        }
        return quotas;
    }

    private boolean isCompletedStatus(String status) {
        return "SUBMITTED".equals(status) || "AUTO_SUBMITTED".equals(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
