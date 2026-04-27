-- ============================================================
-- Smart Adaptive Assessment Platform V3
-- Target schema for the classroom-based teacher/student platform
-- Database: smart_assessment_platform_v3
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_assessment_platform_v3;
USE smart_assessment_platform_v3;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role ENUM('TEACHER', 'STUDENT') NOT NULL,
    status ENUM('ACTIVE', 'INVITED', 'DISABLED') NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255) NULL,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_topics_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_topics_subject_name UNIQUE (subject_id, name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS classrooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_id INT NOT NULL,
    name VARCHAR(120) NOT NULL,
    class_code CHAR(8) NOT NULL UNIQUE,
    academic_term VARCHAR(40) NOT NULL,
    description VARCHAR(255) NULL,
    status ENUM('ACTIVE', 'ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_classrooms_teacher
        FOREIGN KEY (teacher_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_classrooms_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS class_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    classroom_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    joined_via ENUM('CODE', 'MANUAL') NOT NULL,
    status ENUM('ACTIVE', 'REMOVED') NOT NULL DEFAULT 'ACTIVE',
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_class_enrollments_classroom
        FOREIGN KEY (classroom_id) REFERENCES classrooms(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_class_enrollments_student
        FOREIGN KEY (student_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_classroom_student UNIQUE (classroom_id, student_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS study_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_id INT NOT NULL,
    topic_id BIGINT NULL,
    title VARCHAR(180) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_path VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NULL,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    extracted_text LONGTEXT NULL,
    processing_status ENUM('UPLOADED', 'PROCESSED', 'FAILED') NOT NULL DEFAULT 'UPLOADED',
    generated_question_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_study_materials_teacher
        FOREIGN KEY (teacher_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_study_materials_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_study_materials_topic
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS question_import_batches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_id INT NOT NULL,
    source_filename VARCHAR(255) NOT NULL,
    status ENUM('PROCESSING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PROCESSING',
    inserted_count INT NOT NULL DEFAULT 0,
    rejected_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_import_batches_teacher
        FOREIGN KEY (teacher_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_import_batches_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    topic_id BIGINT NOT NULL,
    source_material_id BIGINT NULL,
    import_batch_id BIGINT NULL,
    created_by BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    difficulty_level TINYINT NOT NULL,
    source_type ENUM('MANUAL', 'CSV', 'PDF') NOT NULL DEFAULT 'MANUAL',
    status ENUM('DRAFT', 'REVIEW', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'PUBLISHED',
    explanation TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_questions_topic
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_questions_material
        FOREIGN KEY (source_material_id) REFERENCES study_materials(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_questions_import_batch
        FOREIGN KEY (import_batch_id) REFERENCES question_import_batches(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_questions_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_questions_difficulty CHECK (difficulty_level BETWEEN 1 AND 3)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_key CHAR(1) NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order TINYINT NOT NULL,
    CONSTRAINT fk_question_options_question
        FOREIGN KEY (question_id) REFERENCES questions(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_question_option_key UNIQUE (question_id, option_key),
    CONSTRAINT uq_question_option_order UNIQUE (question_id, sort_order),
    CONSTRAINT chk_option_key CHECK (option_key IN ('A', 'B', 'C', 'D', 'E', 'F'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255) NULL,
    quiz_mode ENUM('ADAPTIVE', 'FIXED') NOT NULL DEFAULT 'ADAPTIVE',
    question_count INT NOT NULL,
    time_limit_minutes INT NOT NULL,
    start_difficulty TINYINT NOT NULL DEFAULT 2,
    min_difficulty TINYINT NOT NULL DEFAULT 1,
    max_difficulty TINYINT NOT NULL DEFAULT 3,
    shuffle_options BOOLEAN NOT NULL DEFAULT TRUE,
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quizzes_teacher
        FOREIGN KEY (teacher_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quizzes_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_quizzes_question_count CHECK (question_count BETWEEN 1 AND 100),
    CONSTRAINT chk_quizzes_time_limit CHECK (time_limit_minutes BETWEEN 1 AND 180),
    CONSTRAINT chk_quizzes_start_difficulty CHECK (start_difficulty BETWEEN 1 AND 3),
    CONSTRAINT chk_quizzes_min_difficulty CHECK (min_difficulty BETWEEN 1 AND 3),
    CONSTRAINT chk_quizzes_max_difficulty CHECK (max_difficulty BETWEEN 1 AND 3)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quiz_topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    target_question_count INT NULL,
    weight_percent DECIMAL(5,2) NULL,
    CONSTRAINT fk_quiz_topics_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quiz_topics_topic
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE RESTRICT,
    CONSTRAINT uq_quiz_topic UNIQUE (quiz_id, topic_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quiz_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    classroom_id BIGINT NOT NULL,
    assigned_by BIGINT NOT NULL,
    available_from DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline_at DATETIME NOT NULL,
    max_attempts TINYINT NOT NULL DEFAULT 1,
    status ENUM('SCHEDULED', 'OPEN', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_assignments_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quiz_assignments_classroom
        FOREIGN KEY (classroom_id) REFERENCES classrooms(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quiz_assignments_assigned_by
        FOREIGN KEY (assigned_by) REFERENCES users(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quiz_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status ENUM('IN_PROGRESS', 'SUBMITTED', 'AUTO_SUBMITTED', 'EXPIRED') NOT NULL DEFAULT 'IN_PROGRESS',
    total_questions_snapshot INT NOT NULL,
    time_limit_minutes_snapshot INT NOT NULL,
    deadline_at_snapshot DATETIME NOT NULL,
    current_question_no INT NOT NULL DEFAULT 0,
    current_difficulty TINYINT NOT NULL DEFAULT 2,
    correct_count INT NOT NULL DEFAULT 0,
    incorrect_count INT NOT NULL DEFAULT 0,
    score_earned DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at DATETIME NULL,
    last_activity_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_sessions_assignment
        FOREIGN KEY (assignment_id) REFERENCES quiz_assignments(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quiz_sessions_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quiz_sessions_student
        FOREIGN KEY (student_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_assignment_student UNIQUE (assignment_id, student_id),
    CONSTRAINT chk_quiz_sessions_difficulty CHECK (current_difficulty BETWEEN 1 AND 3)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS session_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    sequence_no INT NOT NULL,
    served_difficulty TINYINT NOT NULL,
    question_snapshot JSON NOT NULL,
    presented_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    answered_at DATETIME NULL,
    CONSTRAINT fk_session_questions_session
        FOREIGN KEY (session_id) REFERENCES quiz_sessions(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_session_questions_question
        FOREIGN KEY (question_id) REFERENCES questions(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_session_questions_topic
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE RESTRICT,
    CONSTRAINT uq_session_sequence UNIQUE (session_id, sequence_no),
    CONSTRAINT uq_session_question UNIQUE (session_id, question_id),
    CONSTRAINT chk_session_questions_difficulty CHECK (served_difficulty BETWEEN 1 AND 3)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_question_id BIGINT NOT NULL,
    selected_option_key CHAR(1) NULL,
    is_correct BOOLEAN NOT NULL,
    time_spent_seconds INT NOT NULL DEFAULT 0,
    answered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_responses_session_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_response_session_question UNIQUE (session_question_id),
    CONSTRAINT chk_responses_option_key CHECK (
        selected_option_key IS NULL OR selected_option_key IN ('A', 'B', 'C', 'D', 'E', 'F')
    )
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student_topic_mastery (
    student_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    questions_answered INT NOT NULL DEFAULT 0,
    correct_answers INT NOT NULL DEFAULT 0,
    mastery_score DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    last_updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, topic_id),
    CONSTRAINT fk_student_topic_mastery_student
        FOREIGN KEY (student_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_student_topic_mastery_topic
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_topics_subject ON topics(subject_id);
CREATE INDEX idx_classrooms_teacher ON classrooms(teacher_id);
CREATE INDEX idx_class_enrollments_student ON class_enrollments(student_id);
CREATE INDEX idx_questions_subject_topic_diff ON questions(subject_id, topic_id, difficulty_level);
CREATE INDEX idx_questions_status_active ON questions(status, is_active);
CREATE INDEX idx_quizzes_teacher_subject ON quizzes(teacher_id, subject_id);
CREATE INDEX idx_quiz_assignments_classroom_deadline ON quiz_assignments(classroom_id, deadline_at);
CREATE INDEX idx_quiz_sessions_student_status ON quiz_sessions(student_id, status);
CREATE INDEX idx_session_questions_session_answered ON session_questions(session_id, answered_at);

INSERT INTO subjects (code, name, description) VALUES
('OOPJ', 'Object-Oriented Programming (Java)', 'Java OOP concepts, syntax, and design'),
('SE', 'Software Engineering', 'Software process, design, testing, and maintenance'),
('AT', 'Automata Theory', 'Formal languages, automata, and computability'),
('DBMS', 'Database Management Systems', 'Data modeling, SQL, normalization, and transactions')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

INSERT INTO topics (subject_id, name, description, display_order)
SELECT s.id, t.name, t.description, t.display_order
FROM subjects s
JOIN (
    SELECT 'OOPJ' AS subject_code, 'Classes and Objects' AS name, 'Object creation, fields, methods' AS description, 1 AS display_order
    UNION ALL SELECT 'OOPJ', 'Inheritance', 'Base and derived class behavior', 2
    UNION ALL SELECT 'OOPJ', 'Polymorphism', 'Dynamic dispatch and overriding', 3
    UNION ALL SELECT 'OOPJ', 'Abstraction', 'Abstract classes and interfaces', 4
    UNION ALL SELECT 'OOPJ', 'Exception Handling', 'try/catch/finally and custom exceptions', 5
    UNION ALL SELECT 'SE', 'Requirements Engineering', 'Functional and non-functional requirements', 1
    UNION ALL SELECT 'SE', 'Software Development Life Cycle', 'Lifecycle models and phases', 2
    UNION ALL SELECT 'SE', 'UML and Design', 'Use cases, class diagrams, sequence diagrams', 3
    UNION ALL SELECT 'SE', 'Software Testing', 'Levels, techniques, and defect handling', 4
    UNION ALL SELECT 'SE', 'Agile Methods', 'Scrum, iteration planning, team practices', 5
    UNION ALL SELECT 'AT', 'Finite Automata', 'DFA and NFA construction and analysis', 1
    UNION ALL SELECT 'AT', 'Regular Expressions', 'Regex and language equivalence', 2
    UNION ALL SELECT 'AT', 'Context-Free Grammars', 'CFG derivation and simplification', 3
    UNION ALL SELECT 'AT', 'Pushdown Automata', 'PDA design for context-free languages', 4
    UNION ALL SELECT 'AT', 'Turing Machines', 'Computability and decidability', 5
    UNION ALL SELECT 'DBMS', 'ER Modeling', 'Entities, relationships, and cardinality', 1
    UNION ALL SELECT 'DBMS', 'Normalization', '1NF to BCNF and anomaly removal', 2
    UNION ALL SELECT 'DBMS', 'SQL Queries', 'Joins, grouping, filtering, and subqueries', 3
    UNION ALL SELECT 'DBMS', 'Transactions and ACID', 'Consistency, isolation, and recovery', 4
    UNION ALL SELECT 'DBMS', 'Indexing and Concurrency', 'Indexes, locks, and schedule control', 5
) t ON t.subject_code = s.code
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    display_order = VALUES(display_order);
