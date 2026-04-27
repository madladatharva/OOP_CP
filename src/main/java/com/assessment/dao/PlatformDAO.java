package com.assessment.dao;

import com.assessment.model.AssessmentSession;
import com.assessment.model.Classroom;
import com.assessment.model.Question;
import com.assessment.model.QuizAssignment;
import com.assessment.model.SessionQuestion;
import com.assessment.model.Subject;
import com.assessment.model.TeacherQuiz;
import com.assessment.model.Topic;
import com.assessment.model.User;
import com.assessment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlatformDAO {

    public List<Subject> listSubjects() throws SQLException {
        String sql = "SELECT id, code, name FROM subjects ORDER BY name";
        List<Subject> subjects = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(rs.getInt("id"), rs.getString("code"), rs.getString("name")));
            }
        }
        return subjects;
    }

    public Subject findSubjectById(int subjectId) throws SQLException {
        String sql = "SELECT id, code, name FROM subjects WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(rs.getInt("id"), rs.getString("code"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public Subject findSubjectByCodeOrName(String value) throws SQLException {
        String sql = "SELECT id, code, name FROM subjects WHERE code = ? OR name = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, value);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(rs.getInt("id"), rs.getString("code"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public List<Topic> listTopics(Integer subjectId) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT t.id, t.subject_id, s.name AS subject_name, t.name " +
                "FROM topics t JOIN subjects s ON s.id = t.subject_id ");
        List<Topic> topics = new ArrayList<>();
        if (subjectId != null) {
            sql.append("WHERE t.subject_id = ? ");
        }
        sql.append("ORDER BY s.name, t.name");

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (subjectId != null) {
                stmt.setInt(1, subjectId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    topics.add(mapTopic(rs));
                }
            }
        }
        return topics;
    }

    public Topic findTopicById(int topicId) throws SQLException {
        String sql = "SELECT t.id, t.subject_id, s.name AS subject_name, t.name " +
                "FROM topics t JOIN subjects s ON s.id = t.subject_id WHERE t.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapTopic(rs);
                }
            }
        }
        return null;
    }

    public int findOrCreateTopic(int subjectId, String topicName) throws SQLException {
        String findSql = "SELECT id FROM topics WHERE subject_id = ? AND name = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setInt(1, subjectId);
            findStmt.setString(2, topicName);
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insertSql = "INSERT INTO topics (subject_id, name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, subjectId);
            insertStmt.setString(2, topicName);
            insertStmt.executeUpdate();
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create topic: " + topicName);
    }

    public List<Classroom> listTeacherClassrooms(int teacherId) throws SQLException {
        String sql = "SELECT c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at, " +
                "COUNT(cs.id) AS student_count " +
                "FROM classrooms c LEFT JOIN classroom_students cs ON cs.classroom_id = c.id " +
                "WHERE c.teacher_id = ? " +
                "GROUP BY c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at " +
                "ORDER BY c.created_at DESC";
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    classrooms.add(mapClassroom(rs));
                }
            }
        }
        return classrooms;
    }

    public List<Classroom> listStudentClassrooms(int studentId) throws SQLException {
        String sql = "SELECT c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at, " +
                "COUNT(cs2.id) AS student_count " +
                "FROM classrooms c " +
                "JOIN classroom_students cs ON cs.classroom_id = c.id AND cs.student_id = ? " +
                "LEFT JOIN classroom_students cs2 ON cs2.classroom_id = c.id " +
                "GROUP BY c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at " +
                "ORDER BY c.created_at DESC";
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    classrooms.add(mapClassroom(rs));
                }
            }
        }
        return classrooms;
    }

    public int createClassroom(int teacherId, String name, String classCode, String description) throws SQLException {
        String sql = "INSERT INTO classrooms (teacher_id, name, class_code, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, teacherId);
            stmt.setString(2, name);
            stmt.setString(3, classCode);
            stmt.setString(4, description);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create classroom.");
    }

    public Classroom findTeacherClassroom(int teacherId, int classroomId) throws SQLException {
        String sql = "SELECT c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at, " +
                "COUNT(cs.id) AS student_count " +
                "FROM classrooms c LEFT JOIN classroom_students cs ON cs.classroom_id = c.id " +
                "WHERE c.teacher_id = ? AND c.id = ? " +
                "GROUP BY c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            stmt.setInt(2, classroomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapClassroom(rs);
                }
            }
        }
        return null;
    }

    public Classroom findClassroomByCode(String classCode) throws SQLException {
        String sql = "SELECT c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at, " +
                "COUNT(cs.id) AS student_count " +
                "FROM classrooms c LEFT JOIN classroom_students cs ON cs.classroom_id = c.id " +
                "WHERE c.class_code = ? " +
                "GROUP BY c.id, c.teacher_id, c.name, c.class_code, c.description, c.created_at";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapClassroom(rs);
                }
            }
        }
        return null;
    }

    public List<User> listClassroomStudents(int classroomId) throws SQLException {
        String sql = "SELECT u.* FROM classroom_students cs JOIN users u ON u.id = cs.student_id " +
                "WHERE cs.classroom_id = ? ORDER BY u.full_name";
        List<User> students = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classroomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapUser(rs));
                }
            }
        }
        return students;
    }

    public User findStudentByIdentifier(String identifier) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 'STUDENT' AND (username = ? OR email = ?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public boolean isStudentEnrolled(int classroomId, int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM classroom_students WHERE classroom_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classroomId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void enrollStudent(int classroomId, int studentId, String joinedVia) throws SQLException {
        String sql = "INSERT INTO classroom_students (classroom_id, student_id, joined_via) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classroomId);
            stmt.setInt(2, studentId);
            stmt.setString(3, joinedVia);
            stmt.executeUpdate();
        }
    }

    public List<Question> listQuestions(Integer subjectId, Integer topicId, Integer difficultyLevel) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT q.*, s.name AS subject_name, t.name AS topic_name " +
                "FROM questions q " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN topics t ON t.id = q.topic_id " +
                "WHERE q.is_active = TRUE ");
        List<Object> params = new ArrayList<>();
        if (subjectId != null) {
            sql.append("AND q.subject_id = ? ");
            params.add(subjectId);
        }
        if (topicId != null) {
            sql.append("AND q.topic_id = ? ");
            params.add(topicId);
        }
        if (difficultyLevel != null) {
            sql.append("AND q.difficulty_level = ? ");
            params.add(difficultyLevel);
        }
        sql.append("ORDER BY s.name, t.name, q.difficulty_level, q.created_at DESC");

        List<Question> questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            bindParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapQuestion(rs));
                }
            }
        }
        return questions;
    }

    public Question findQuestionById(int questionId) throws SQLException {
        String sql = "SELECT q.*, s.name AS subject_name, t.name AS topic_name " +
                "FROM questions q " +
                "LEFT JOIN subjects s ON s.id = q.subject_id " +
                "LEFT JOIN topics t ON t.id = q.topic_id WHERE q.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }
        }
        return null;
    }

    public int insertQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions " +
                "(subject_id, topic_id, question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, is_active, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, question.getSubjectId());
            stmt.setInt(2, question.getTopicId());
            stmt.setString(3, question.getQuestionText());
            stmt.setString(4, question.getOptionA());
            stmt.setString(5, question.getOptionB());
            stmt.setString(6, question.getOptionC());
            stmt.setString(7, question.getOptionD());
            stmt.setString(8, question.getCorrectOption());
            stmt.setInt(9, question.getDifficultyLevel());
            stmt.setString(10, question.getCategory());
            stmt.setBoolean(11, true);
            stmt.setInt(12, question.getCreatedBy());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert question.");
    }

    public boolean updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET subject_id = ?, topic_id = ?, question_text = ?, option_a = ?, option_b = ?, option_c = ?, " +
                "option_d = ?, correct_option = ?, difficulty_level = ?, category = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, question.getSubjectId());
            stmt.setInt(2, question.getTopicId());
            stmt.setString(3, question.getQuestionText());
            stmt.setString(4, question.getOptionA());
            stmt.setString(5, question.getOptionB());
            stmt.setString(6, question.getOptionC());
            stmt.setString(7, question.getOptionD());
            stmt.setString(8, question.getCorrectOption());
            stmt.setInt(9, question.getDifficultyLevel());
            stmt.setString(10, question.getCategory());
            stmt.setInt(11, question.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public void archiveQuestion(int questionId) throws SQLException {
        String sql = "UPDATE questions SET is_active = FALSE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }

    public int countActiveQuestions(int subjectId, List<Integer> topicIds) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM questions WHERE is_active = TRUE AND subject_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(subjectId);
        if (topicIds != null && !topicIds.isEmpty()) {
            sql.append(" AND topic_id IN (").append(placeholders(topicIds.size())).append(")");
            params.addAll(topicIds);
        }
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            bindParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int createQuiz(TeacherQuiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (teacher_id, subject_id, title, description, question_count, time_limit_minutes, start_difficulty) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quiz.getTeacherId());
            stmt.setInt(2, quiz.getSubjectId());
            stmt.setString(3, quiz.getTitle());
            stmt.setString(4, quiz.getDescription());
            stmt.setInt(5, quiz.getQuestionCount());
            stmt.setInt(6, quiz.getTimeLimitMinutes());
            stmt.setInt(7, quiz.getStartDifficulty());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create quiz.");
    }

    public void replaceQuizTopics(int quizId, List<Integer> topicIds) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM quiz_topics WHERE quiz_id = ?")) {
                deleteStmt.setInt(1, quizId);
                deleteStmt.executeUpdate();
            }
            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO quiz_topics (quiz_id, topic_id) VALUES (?, ?)")) {
                for (Integer topicId : topicIds) {
                    insertStmt.setInt(1, quizId);
                    insertStmt.setInt(2, topicId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            conn.commit();
        }
    }

    public List<TeacherQuiz> listTeacherQuizzes(int teacherId) throws SQLException {
        String sql = "SELECT q.id, q.teacher_id, q.subject_id, s.name AS subject_name, q.title, q.description, " +
                "q.question_count, q.time_limit_minutes, q.start_difficulty, q.created_at, " +
                "GROUP_CONCAT(t.name ORDER BY t.name SEPARATOR ', ') AS topic_summary, " +
                "COUNT(DISTINCT qa.id) AS assignment_count " +
                "FROM quizzes q " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "LEFT JOIN quiz_topics qt ON qt.quiz_id = q.id " +
                "LEFT JOIN topics t ON t.id = qt.topic_id " +
                "LEFT JOIN quiz_assignments qa ON qa.quiz_id = q.id " +
                "WHERE q.teacher_id = ? " +
                "GROUP BY q.id, q.teacher_id, q.subject_id, s.name, q.title, q.description, q.question_count, q.time_limit_minutes, q.start_difficulty, q.created_at " +
                "ORDER BY q.created_at DESC";
        List<TeacherQuiz> quizzes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TeacherQuiz quiz = new TeacherQuiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setTeacherId(rs.getInt("teacher_id"));
                    quiz.setSubjectId(rs.getInt("subject_id"));
                    quiz.setSubjectName(rs.getString("subject_name"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));
                    quiz.setQuestionCount(rs.getInt("question_count"));
                    quiz.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
                    quiz.setStartDifficulty(rs.getInt("start_difficulty"));
                    quiz.setTopicSummary(rs.getString("topic_summary"));
                    quiz.setAssignmentCount(rs.getInt("assignment_count"));
                    quiz.setCreatedAt(rs.getTimestamp("created_at"));
                    quizzes.add(quiz);
                }
            }
        }
        return quizzes;
    }

    public TeacherQuiz findTeacherQuiz(int teacherId, int quizId) throws SQLException {
        String sql = "SELECT q.id, q.teacher_id, q.subject_id, s.name AS subject_name, q.title, q.description, " +
                "q.question_count, q.time_limit_minutes, q.start_difficulty, q.created_at " +
                "FROM quizzes q JOIN subjects s ON s.id = q.subject_id WHERE q.teacher_id = ? AND q.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            stmt.setInt(2, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TeacherQuiz quiz = new TeacherQuiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setTeacherId(rs.getInt("teacher_id"));
                    quiz.setSubjectId(rs.getInt("subject_id"));
                    quiz.setSubjectName(rs.getString("subject_name"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));
                    quiz.setQuestionCount(rs.getInt("question_count"));
                    quiz.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
                    quiz.setStartDifficulty(rs.getInt("start_difficulty"));
                    quiz.setCreatedAt(rs.getTimestamp("created_at"));
                    return quiz;
                }
            }
        }
        return null;
    }

    public TeacherQuiz findQuizById(int quizId) throws SQLException {
        String sql = "SELECT q.id, q.teacher_id, q.subject_id, s.name AS subject_name, q.title, q.description, " +
                "q.question_count, q.time_limit_minutes, q.start_difficulty, q.created_at " +
                "FROM quizzes q JOIN subjects s ON s.id = q.subject_id WHERE q.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TeacherQuiz quiz = new TeacherQuiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setTeacherId(rs.getInt("teacher_id"));
                    quiz.setSubjectId(rs.getInt("subject_id"));
                    quiz.setSubjectName(rs.getString("subject_name"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));
                    quiz.setQuestionCount(rs.getInt("question_count"));
                    quiz.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
                    quiz.setStartDifficulty(rs.getInt("start_difficulty"));
                    quiz.setCreatedAt(rs.getTimestamp("created_at"));
                    return quiz;
                }
            }
        }
        return null;
    }

    public List<Integer> listQuizTopicIds(int quizId) throws SQLException {
        String sql = "SELECT topic_id FROM quiz_topics WHERE quiz_id = ? ORDER BY topic_id";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("topic_id"));
                }
            }
        }
        return ids;
    }

    public int createAssignment(int quizId, int classroomId, Timestamp deadlineAt) throws SQLException {
        String sql = "INSERT INTO quiz_assignments (quiz_id, classroom_id, deadline_at) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quizId);
            stmt.setInt(2, classroomId);
            stmt.setTimestamp(3, deadlineAt);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create assignment.");
    }

    public List<QuizAssignment> listTeacherAssignments(int teacherId) throws SQLException {
        String sql = "SELECT qa.id, qa.quiz_id, qa.classroom_id, q.title AS quiz_title, c.name AS classroom_name, s.name AS subject_name, " +
                "q.question_count, q.time_limit_minutes, qa.available_from, qa.deadline_at, " +
                "NULL AS session_status, NULL AS session_id, NULL AS submitted_at, " +
                "AVG(CASE WHEN sess.total_questions > 0 THEN (sess.score * 100.0 / sess.total_questions) END) AS score_percentage, " +
                "COUNT(sess.id) AS answered_count " +
                "FROM quiz_assignments qa " +
                "JOIN quizzes q ON q.id = qa.quiz_id " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN classrooms c ON c.id = qa.classroom_id " +
                "LEFT JOIN assessment_sessions sess ON sess.assignment_id = qa.id " +
                "WHERE q.teacher_id = ? " +
                "GROUP BY qa.id, qa.quiz_id, qa.classroom_id, q.title, c.name, s.name, q.question_count, q.time_limit_minutes, qa.available_from, qa.deadline_at " +
                "ORDER BY qa.deadline_at ASC";
        List<QuizAssignment> assignments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapAssignment(rs));
                }
            }
        }
        return assignments;
    }

    public List<QuizAssignment> listStudentAssignments(int studentId) throws SQLException {
        String sql = "SELECT qa.id, qa.quiz_id, qa.classroom_id, q.title AS quiz_title, c.name AS classroom_name, s.name AS subject_name, " +
                "q.question_count, q.time_limit_minutes, qa.available_from, qa.deadline_at, " +
                "sess.status AS session_status, sess.id AS session_id, sess.current_question_number AS answered_count, " +
                "CASE WHEN sess.total_questions > 0 THEN (sess.score * 100.0 / sess.total_questions) END AS score_percentage, " +
                "sess.submitted_at " +
                "FROM quiz_assignments qa " +
                "JOIN quizzes q ON q.id = qa.quiz_id " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN classrooms c ON c.id = qa.classroom_id " +
                "JOIN classroom_students cs ON cs.classroom_id = c.id " +
                "LEFT JOIN assessment_sessions sess ON sess.assignment_id = qa.id AND sess.student_id = cs.student_id " +
                "WHERE cs.student_id = ? " +
                "ORDER BY qa.deadline_at ASC";
        List<QuizAssignment> assignments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapAssignment(rs));
                }
            }
        }
        return assignments;
    }

    public QuizAssignment findStudentAssignment(int assignmentId, int studentId) throws SQLException {
        String sql = "SELECT qa.id, qa.quiz_id, qa.classroom_id, q.title AS quiz_title, c.name AS classroom_name, s.name AS subject_name, " +
                "q.question_count, q.time_limit_minutes, qa.available_from, qa.deadline_at, " +
                "sess.status AS session_status, sess.id AS session_id, sess.current_question_number AS answered_count, " +
                "CASE WHEN sess.total_questions > 0 THEN (sess.score * 100.0 / sess.total_questions) END AS score_percentage, " +
                "sess.submitted_at " +
                "FROM quiz_assignments qa " +
                "JOIN quizzes q ON q.id = qa.quiz_id " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN classrooms c ON c.id = qa.classroom_id " +
                "JOIN classroom_students cs ON cs.classroom_id = c.id " +
                "LEFT JOIN assessment_sessions sess ON sess.assignment_id = qa.id AND sess.student_id = cs.student_id " +
                "WHERE qa.id = ? AND cs.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAssignment(rs);
                }
            }
        }
        return null;
    }

    public int createAssessmentSession(AssessmentSession session) throws SQLException {
        String sql = "INSERT INTO assessment_sessions " +
                "(assignment_id, student_id, quiz_id, current_question_number, total_questions, current_difficulty, score, status, time_limit_minutes, started_at, last_activity_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, session.getAssignmentId());
            stmt.setInt(2, session.getStudentId());
            stmt.setInt(3, session.getQuizId());
            stmt.setInt(4, session.getCurrentQuestionNumber());
            stmt.setInt(5, session.getTotalQuestions());
            stmt.setInt(6, session.getCurrentDifficulty());
            stmt.setInt(7, session.getScore());
            stmt.setString(8, session.getStatus());
            stmt.setInt(9, session.getTimeLimitMinutes());
            stmt.setTimestamp(10, session.getStartedAt());
            stmt.setTimestamp(11, session.getLastActivityAt());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create assessment session.");
    }

    public AssessmentSession findAssessmentSession(int sessionId, int studentId) throws SQLException {
        String sql = "SELECT sess.*, q.title AS quiz_title, s.name AS subject_name, qa.deadline_at " +
                "FROM assessment_sessions sess " +
                "JOIN quizzes q ON q.id = sess.quiz_id " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN quiz_assignments qa ON qa.id = sess.assignment_id " +
                "WHERE sess.id = ? AND sess.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAssessmentSession(rs);
                }
            }
        }
        return null;
    }

    public AssessmentSession findAssessmentSessionForAssignment(int assignmentId, int studentId) throws SQLException {
        String sql = "SELECT sess.*, q.title AS quiz_title, s.name AS subject_name, qa.deadline_at " +
                "FROM assessment_sessions sess " +
                "JOIN quizzes q ON q.id = sess.quiz_id " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN quiz_assignments qa ON qa.id = sess.assignment_id " +
                "WHERE sess.assignment_id = ? AND sess.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAssessmentSession(rs);
                }
            }
        }
        return null;
    }

    public void updateAssessmentSession(AssessmentSession session) throws SQLException {
        String sql = "UPDATE assessment_sessions SET current_question_number = ?, current_difficulty = ?, score = ?, status = ?, submitted_at = ?, last_activity_at = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getCurrentQuestionNumber());
            stmt.setInt(2, session.getCurrentDifficulty());
            stmt.setInt(3, session.getScore());
            stmt.setString(4, session.getStatus());
            stmt.setTimestamp(5, session.getSubmittedAt());
            stmt.setTimestamp(6, session.getLastActivityAt());
            stmt.setInt(7, session.getId());
            stmt.executeUpdate();
        }
    }

    public SessionQuestion findUnansweredSessionQuestion(int sessionId) throws SQLException {
        String sql = "SELECT sq.*, r.selected_option, r.is_correct " +
                "FROM assessment_session_questions sq " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE sq.session_id = ? AND r.id IS NULL " +
                "ORDER BY sq.question_order DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSessionQuestion(rs);
                }
            }
        }
        return null;
    }

    public int insertSessionQuestion(int sessionId, Question question, int questionOrder, int servedDifficulty) throws SQLException {
        String sql = "INSERT INTO assessment_session_questions " +
                "(session_id, question_id, question_order, served_difficulty, subject_name, topic_name, question_text, option_a, option_b, option_c, option_d, correct_option) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, question.getId());
            stmt.setInt(3, questionOrder);
            stmt.setInt(4, servedDifficulty);
            stmt.setString(5, question.getSubjectName());
            stmt.setString(6, question.getTopicName());
            stmt.setString(7, question.getQuestionText());
            stmt.setString(8, question.getOptionA());
            stmt.setString(9, question.getOptionB());
            stmt.setString(10, question.getOptionC());
            stmt.setString(11, question.getOptionD());
            stmt.setString(12, question.getCorrectOption());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert session question.");
    }

    public SessionQuestion findSessionQuestion(int sessionQuestionId, int studentId) throws SQLException {
        String sql = "SELECT sq.*, r.selected_option, r.is_correct " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE sq.id = ? AND sess.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionQuestionId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSessionQuestion(rs);
                }
            }
        }
        return null;
    }

    public void markSessionQuestionAnswered(int sessionQuestionId, Timestamp answeredAt) throws SQLException {
        String sql = "UPDATE assessment_session_questions SET answered_at = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, answeredAt);
            stmt.setInt(2, sessionQuestionId);
            stmt.executeUpdate();
        }
    }

    public void insertResponse(int sessionQuestionId, String selectedOption, boolean correct) throws SQLException {
        String sql = "INSERT INTO assessment_responses (session_question_id, selected_option, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionQuestionId);
            stmt.setString(2, selectedOption);
            stmt.setBoolean(3, correct);
            stmt.executeUpdate();
        }
    }

    public Map<Integer, Integer> countServedQuestionsByTopic(int sessionId) throws SQLException {
        String sql = "SELECT q.topic_id, COUNT(*) AS served_count " +
                "FROM assessment_session_questions sq JOIN questions q ON q.id = sq.question_id " +
                "WHERE sq.session_id = ? GROUP BY q.topic_id";
        Map<Integer, Integer> counts = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getInt("topic_id"), rs.getInt("served_count"));
                }
            }
        }
        return counts;
    }

    public List<Integer> listServedQuestionIds(int sessionId) throws SQLException {
        String sql = "SELECT question_id FROM assessment_session_questions WHERE session_id = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("question_id"));
                }
            }
        }
        return ids;
    }

    public Question findAdaptiveQuestionCandidate(int subjectId, int topicId, List<Integer> excludedQuestionIds,
            int targetDifficulty) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT q.*, s.name AS subject_name, t.name AS topic_name " +
                "FROM questions q " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "JOIN topics t ON t.id = q.topic_id " +
                "WHERE q.is_active = TRUE AND q.subject_id = ? AND q.topic_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(subjectId);
        params.add(topicId);
        if (excludedQuestionIds != null && !excludedQuestionIds.isEmpty()) {
            sql.append("AND q.id NOT IN (").append(placeholders(excludedQuestionIds.size())).append(") ");
            params.addAll(excludedQuestionIds);
        }
        sql.append("ORDER BY CASE WHEN q.difficulty_level = ? THEN 0 ELSE 1 END, ABS(q.difficulty_level - ?), RAND() LIMIT 1");
        params.add(targetDifficulty);
        params.add(targetDifficulty);

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            bindParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }
        }
        return null;
    }

    public List<SessionQuestion> listSessionResultQuestions(int sessionId, int studentId) throws SQLException {
        String sql = "SELECT sq.*, r.selected_option, r.is_correct " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE sq.session_id = ? AND sess.student_id = ? " +
                "ORDER BY sq.question_order";
        List<SessionQuestion> questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapSessionQuestion(rs));
                }
            }
        }
        return questions;
    }

    public List<Map<String, Object>> getTeacherClassPerformance(int teacherId) throws SQLException {
        String sql = "SELECT c.name AS class_name, c.class_code, COUNT(DISTINCT qa.id) AS assigned_quizzes, " +
                "COUNT(DISTINCT cs.student_id) AS students, " +
                "AVG(CASE WHEN sess.total_questions > 0 THEN (sess.score * 100.0 / sess.total_questions) END) AS average_score " +
                "FROM classrooms c " +
                "LEFT JOIN classroom_students cs ON cs.classroom_id = c.id " +
                "LEFT JOIN quiz_assignments qa ON qa.classroom_id = c.id " +
                "LEFT JOIN assessment_sessions sess ON sess.assignment_id = qa.id " +
                "WHERE c.teacher_id = ? " +
                "GROUP BY c.id, c.name, c.class_code " +
                "ORDER BY average_score ASC, c.name";
        return executeAnalyticsQuery(sql, teacherId);
    }

    public List<Map<String, Object>> getTeacherQuestionAccuracy(int teacherId) throws SQLException {
        String sql = "SELECT sq.question_text, sq.topic_name, sq.subject_name, COUNT(r.id) AS attempts, " +
                "AVG(CASE WHEN r.is_correct THEN 100 ELSE 0 END) AS accuracy " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "JOIN quiz_assignments qa ON qa.id = sess.assignment_id " +
                "JOIN quizzes qz ON qz.id = qa.quiz_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE qz.teacher_id = ? " +
                "GROUP BY sq.question_text, sq.topic_name, sq.subject_name " +
                "HAVING COUNT(r.id) > 0 " +
                "ORDER BY accuracy ASC, attempts DESC, sq.question_text " +
                "LIMIT 10";
        return executeAnalyticsQuery(sql, teacherId);
    }

    public List<Map<String, Object>> getTeacherWeakTopics(int teacherId) throws SQLException {
        String sql = "SELECT sq.subject_name, sq.topic_name, COUNT(r.id) AS attempts, " +
                "AVG(CASE WHEN r.is_correct THEN 100 ELSE 0 END) AS accuracy " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "JOIN quiz_assignments qa ON qa.id = sess.assignment_id " +
                "JOIN quizzes qz ON qz.id = qa.quiz_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE qz.teacher_id = ? " +
                "GROUP BY sq.subject_name, sq.topic_name " +
                "HAVING COUNT(r.id) > 0 " +
                "ORDER BY accuracy ASC, attempts DESC " +
                "LIMIT 10";
        return executeAnalyticsQuery(sql, teacherId);
    }

    public List<Map<String, Object>> getStudentSubjectPerformance(int studentId) throws SQLException {
        String sql = "SELECT sq.subject_name, COUNT(r.id) AS attempts, " +
                "AVG(CASE WHEN r.is_correct THEN 100 ELSE 0 END) AS accuracy " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE sess.student_id = ? " +
                "GROUP BY sq.subject_name " +
                "HAVING COUNT(r.id) > 0 " +
                "ORDER BY sq.subject_name";
        return executeAnalyticsQuery(sql, studentId);
    }

    public List<Map<String, Object>> getStudentTopicPerformance(int studentId) throws SQLException {
        String sql = "SELECT sq.subject_name, sq.topic_name, COUNT(r.id) AS attempts, " +
                "AVG(CASE WHEN r.is_correct THEN 100 ELSE 0 END) AS accuracy " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE sess.student_id = ? " +
                "GROUP BY sq.subject_name, sq.topic_name " +
                "HAVING COUNT(r.id) > 0 " +
                "ORDER BY accuracy ASC, sq.subject_name, sq.topic_name";
        return executeAnalyticsQuery(sql, studentId);
    }

    public List<Map<String, Object>> getStudentRecentResults(int studentId) throws SQLException {
        String sql = "SELECT q.title AS quiz_title, s.name AS subject_name, sess.status, sess.started_at, sess.submitted_at, " +
                "sess.current_question_number AS answered_count, sess.total_questions, " +
                "CASE WHEN sess.total_questions > 0 THEN (sess.score * 100.0 / sess.total_questions) END AS accuracy " +
                "FROM assessment_sessions sess " +
                "JOIN quizzes q ON q.id = sess.quiz_id " +
                "JOIN subjects s ON s.id = q.subject_id " +
                "WHERE sess.student_id = ? " +
                "ORDER BY sess.started_at DESC " +
                "LIMIT 10";
        return executeAnalyticsQuery(sql, studentId);
    }

    public List<Map<String, Object>> getSessionTopicPerformance(int sessionId, int studentId) throws SQLException {
        String sql = "SELECT sq.subject_name, sq.topic_name, COUNT(r.id) AS attempts, " +
                "SUM(CASE WHEN r.is_correct THEN 1 ELSE 0 END) AS correct_answers, " +
                "AVG(CASE WHEN r.is_correct THEN 100 ELSE 0 END) AS accuracy " +
                "FROM assessment_session_questions sq " +
                "JOIN assessment_sessions sess ON sess.id = sq.session_id " +
                "LEFT JOIN assessment_responses r ON r.session_question_id = sq.id " +
                "WHERE sq.session_id = ? AND sess.student_id = ? " +
                "GROUP BY sq.subject_name, sq.topic_name " +
                "ORDER BY accuracy ASC, sq.topic_name";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("subject_name", rs.getString("subject_name"));
                    row.put("topic_name", rs.getString("topic_name"));
                    row.put("attempts", rs.getInt("attempts"));
                    row.put("correct_answers", rs.getInt("correct_answers"));
                    row.put("accuracy", rs.getDouble("accuracy"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private List<Map<String, Object>> executeAnalyticsQuery(String sql, int idParam) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    int columnCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private void bindParams(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
    }

    private String placeholders(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("?");
        }
        return builder.toString();
    }

    private Topic mapTopic(ResultSet rs) throws SQLException {
        return new Topic(
                rs.getInt("id"),
                rs.getInt("subject_id"),
                rs.getString("subject_name"),
                rs.getString("name"));
    }

    private Classroom mapClassroom(ResultSet rs) throws SQLException {
        return new Classroom(
                rs.getInt("id"),
                rs.getInt("teacher_id"),
                rs.getString("name"),
                rs.getString("class_code"),
                rs.getString("description"),
                rs.getInt("student_count"),
                rs.getTimestamp("created_at"));
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("full_name"),
                rs.getString("role"),
                rs.getTimestamp("created_at"));
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        int subjectId = rs.getInt("subject_id");
        question.setSubjectId(rs.wasNull() ? null : subjectId);
        int topicId = rs.getInt("topic_id");
        question.setTopicId(rs.wasNull() ? null : topicId);
        question.setSubjectName(rs.getString("subject_name"));
        question.setTopicName(rs.getString("topic_name"));
        question.setQuestionText(rs.getString("question_text"));
        question.setOptionA(rs.getString("option_a"));
        question.setOptionB(rs.getString("option_b"));
        question.setOptionC(rs.getString("option_c"));
        question.setOptionD(rs.getString("option_d"));
        question.setCorrectOption(rs.getString("correct_option"));
        question.setDifficultyLevel(rs.getInt("difficulty_level"));
        question.setCategory(rs.getString("category"));
        question.setCreatedBy(rs.getInt("created_by"));
        question.setCreatedAt(rs.getTimestamp("created_at"));
        return question;
    }

    private QuizAssignment mapAssignment(ResultSet rs) throws SQLException {
        QuizAssignment assignment = new QuizAssignment();
        assignment.setId(rs.getInt("id"));
        assignment.setQuizId(rs.getInt("quiz_id"));
        assignment.setClassroomId(rs.getInt("classroom_id"));
        assignment.setQuizTitle(rs.getString("quiz_title"));
        assignment.setClassroomName(rs.getString("classroom_name"));
        assignment.setSubjectName(rs.getString("subject_name"));
        assignment.setQuestionCount(rs.getInt("question_count"));
        assignment.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
        assignment.setAvailableFrom(rs.getTimestamp("available_from"));
        assignment.setDeadlineAt(rs.getTimestamp("deadline_at"));
        assignment.setSessionStatus(rs.getString("session_status"));
        int sessionId = rs.getInt("session_id");
        assignment.setSessionId(rs.wasNull() ? null : sessionId);
        double score = rs.getDouble("score_percentage");
        assignment.setScorePercentage(rs.wasNull() ? null : score);
        int answeredCount = rs.getInt("answered_count");
        assignment.setAnsweredCount(rs.wasNull() ? null : answeredCount);
        assignment.setSubmittedAt(rs.getTimestamp("submitted_at"));
        return assignment;
    }

    private AssessmentSession mapAssessmentSession(ResultSet rs) throws SQLException {
        AssessmentSession session = new AssessmentSession();
        session.setId(rs.getInt("id"));
        session.setAssignmentId(rs.getInt("assignment_id"));
        session.setStudentId(rs.getInt("student_id"));
        session.setQuizId(rs.getInt("quiz_id"));
        session.setQuizTitle(rs.getString("quiz_title"));
        session.setSubjectName(rs.getString("subject_name"));
        session.setCurrentQuestionNumber(rs.getInt("current_question_number"));
        session.setTotalQuestions(rs.getInt("total_questions"));
        session.setCurrentDifficulty(rs.getInt("current_difficulty"));
        session.setScore(rs.getInt("score"));
        session.setStatus(rs.getString("status"));
        session.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
        session.setStartedAt(rs.getTimestamp("started_at"));
        session.setSubmittedAt(rs.getTimestamp("submitted_at"));
        session.setLastActivityAt(rs.getTimestamp("last_activity_at"));
        session.setDeadlineAt(rs.getTimestamp("deadline_at"));
        return session;
    }

    private SessionQuestion mapSessionQuestion(ResultSet rs) throws SQLException {
        SessionQuestion question = new SessionQuestion();
        question.setId(rs.getInt("id"));
        question.setSessionId(rs.getInt("session_id"));
        question.setQuestionId(rs.getInt("question_id"));
        question.setQuestionOrder(rs.getInt("question_order"));
        question.setServedDifficulty(rs.getInt("served_difficulty"));
        question.setSubjectName(rs.getString("subject_name"));
        question.setTopicName(rs.getString("topic_name"));
        question.setQuestionText(rs.getString("question_text"));
        question.setOptionA(rs.getString("option_a"));
        question.setOptionB(rs.getString("option_b"));
        question.setOptionC(rs.getString("option_c"));
        question.setOptionD(rs.getString("option_d"));
        question.setCorrectOption(rs.getString("correct_option"));
        question.setSelectedOption(rs.getString("selected_option"));
        boolean correct = rs.getBoolean("is_correct");
        question.setCorrect(rs.wasNull() ? null : correct);
        question.setPresentedAt(rs.getTimestamp("presented_at"));
        question.setAnsweredAt(rs.getTimestamp("answered_at"));
        return question;
    }
}
