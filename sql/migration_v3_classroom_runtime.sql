USE smart_assessment_db;

ALTER TABLE users
    MODIFY COLUMN role ENUM('USER', 'ADMIN', 'TEACHER', 'STUDENT') NOT NULL DEFAULT 'STUDENT';

UPDATE users SET role = 'TEACHER' WHERE role = 'ADMIN';
UPDATE users SET role = 'STUDENT' WHERE role = 'USER';

ALTER TABLE users
    MODIFY COLUMN role ENUM('TEACHER', 'STUDENT') NOT NULL DEFAULT 'STUDENT';

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'questions'
          AND COLUMN_NAME = 'subject_id'
    ),
    'SELECT 1',
    'ALTER TABLE questions ADD COLUMN subject_id INT NULL AFTER id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'questions'
          AND COLUMN_NAME = 'topic_id'
    ),
    'SELECT 1',
    'ALTER TABLE questions ADD COLUMN topic_id INT NULL AFTER subject_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'questions'
          AND COLUMN_NAME = 'is_active'
    ),
    'SELECT 1',
    'ALTER TABLE questions ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE AFTER category'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS topics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_topics_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uq_subject_topic UNIQUE (subject_id, name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS classrooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    name VARCHAR(120) NOT NULL,
    class_code VARCHAR(12) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_classrooms_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS classroom_students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    classroom_id INT NOT NULL,
    student_id INT NOT NULL,
    joined_via ENUM('CODE', 'MANUAL') NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_classroom_students_class FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_classroom_students_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_classroom_student UNIQUE (classroom_id, student_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    subject_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255) NULL,
    question_count INT NOT NULL,
    time_limit_minutes INT NOT NULL,
    start_difficulty INT NOT NULL DEFAULT 2,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quizzes_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_quizzes_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quiz_topics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    topic_id INT NOT NULL,
    CONSTRAINT fk_quiz_topics_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_topics_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    CONSTRAINT uq_quiz_topic UNIQUE (quiz_id, topic_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quiz_assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    classroom_id INT NOT NULL,
    available_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignments_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    assignment_id INT NOT NULL,
    student_id INT NOT NULL,
    quiz_id INT NOT NULL,
    current_question_number INT NOT NULL DEFAULT 0,
    total_questions INT NOT NULL,
    current_difficulty INT NOT NULL DEFAULT 2,
    score INT NOT NULL DEFAULT 0,
    status ENUM('IN_PROGRESS', 'SUBMITTED', 'AUTO_SUBMITTED', 'MISSED') NOT NULL DEFAULT 'IN_PROGRESS',
    time_limit_minutes INT NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    last_activity_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessions_assignment FOREIGN KEY (assignment_id) REFERENCES quiz_assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_sessions_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sessions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    CONSTRAINT uq_assignment_student_session UNIQUE (assignment_id, student_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_session_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    question_id INT NOT NULL,
    question_order INT NOT NULL,
    served_difficulty INT NOT NULL,
    subject_name VARCHAR(120) NOT NULL,
    topic_name VARCHAR(120) NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    presented_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    answered_at TIMESTAMP NULL,
    CONSTRAINT fk_session_questions_session FOREIGN KEY (session_id) REFERENCES assessment_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_session_questions_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE RESTRICT,
    CONSTRAINT uq_session_question_order UNIQUE (session_id, question_order),
    CONSTRAINT uq_session_question UNIQUE (session_id, question_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assessment_responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_question_id INT NOT NULL,
    selected_option CHAR(1) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    time_spent_seconds INT NOT NULL DEFAULT 0,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assessment_responses_question FOREIGN KEY (session_question_id) REFERENCES assessment_session_questions(id) ON DELETE CASCADE,
    CONSTRAINT uq_response_per_question UNIQUE (session_question_id)
) ENGINE=InnoDB;

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'questions'
          AND INDEX_NAME = 'idx_questions_subject_topic_difficulty'
    ),
    'SELECT 1',
    'CREATE INDEX idx_questions_subject_topic_difficulty ON questions(subject_id, topic_id, difficulty_level)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'classrooms'
          AND INDEX_NAME = 'idx_classroom_teacher'
    ),
    'SELECT 1',
    'CREATE INDEX idx_classroom_teacher ON classrooms(teacher_id)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'quiz_assignments'
          AND INDEX_NAME = 'idx_assignment_classroom'
    ),
    'SELECT 1',
    'CREATE INDEX idx_assignment_classroom ON quiz_assignments(classroom_id, deadline_at)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'assessment_sessions'
          AND INDEX_NAME = 'idx_session_student'
    ),
    'SELECT 1',
    'CREATE INDEX idx_session_student ON assessment_sessions(student_id, status)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO users (username, password, email, full_name, role)
SELECT 'teacher_demo', 'teacher123', 'teacher@assessment.com', 'Teacher Demo', 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'teacher_demo');

INSERT INTO users (username, password, email, full_name, role)
SELECT 'student_demo', 'student123', 'student@assessment.com', 'Student Demo', 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'student_demo');

INSERT INTO subjects (code, name) VALUES
('OOPJ', 'Object-Oriented Programming (Java)'),
('SE', 'Software Engineering'),
('AT', 'Automata Theory'),
('DBMS', 'Database Management Systems (DBMS)')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO topics (subject_id, name)
SELECT s.id, t.name
FROM subjects s
JOIN (
    SELECT 'OOPJ' AS code, 'Inheritance' AS name
    UNION ALL SELECT 'OOPJ', 'Polymorphism'
    UNION ALL SELECT 'OOPJ', 'Abstraction'
    UNION ALL SELECT 'SE', 'Requirements Engineering'
    UNION ALL SELECT 'SE', 'Software Testing'
    UNION ALL SELECT 'SE', 'UML'
    UNION ALL SELECT 'AT', 'DFA'
    UNION ALL SELECT 'AT', 'NFA'
    UNION ALL SELECT 'AT', 'Regular Expressions'
    UNION ALL SELECT 'DBMS', 'Normalization'
    UNION ALL SELECT 'DBMS', 'Transactions'
    UNION ALL SELECT 'DBMS', 'SQL Joins'
) t ON s.code = t.code
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO questions (subject_id, topic_id, question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, is_active, created_by)
SELECT s.id, t.id, seed.question_text, seed.option_a, seed.option_b, seed.option_c, seed.option_d, seed.correct_option, seed.difficulty_level, seed.category, TRUE,
       (SELECT id FROM users WHERE username = 'teacher_demo' LIMIT 1)
FROM (
    SELECT 'OOPJ' AS subject_code, 'Inheritance' AS topic_name, 1 AS difficulty_level, 'Object state from parent to child is enabled through which concept?' AS question_text, 'Encapsulation' AS option_a, 'Inheritance' AS option_b, 'Abstraction' AS option_c, 'Overloading' AS option_d, 'B' AS correct_option, 'Object-Oriented Programming (Java)' AS category
    UNION ALL SELECT 'OOPJ', 'Polymorphism', 2, 'Method overriding is most closely related to which OOP principle?', 'Composition', 'Polymorphism', 'Aggregation', 'Instantiation', 'B', 'Object-Oriented Programming (Java)'
    UNION ALL SELECT 'OOPJ', 'Abstraction', 3, 'Which Java feature best supports abstraction in large systems?', 'Global variables', 'Abstract classes and interfaces', 'Primitive casting', 'Package renaming', 'B', 'Object-Oriented Programming (Java)'
    UNION ALL SELECT 'SE', 'Requirements Engineering', 1, 'A non-functional requirement usually describes which aspect?', 'Data structure choice', 'System quality attributes', 'Loop syntax', 'Table joins', 'B', 'Software Engineering'
    UNION ALL SELECT 'SE', 'Software Testing', 2, 'Unit testing focuses on what scope?', 'Entire organization', 'Small isolated code units', 'Only production data', 'Network topology', 'B', 'Software Engineering'
    UNION ALL SELECT 'SE', 'UML', 3, 'Which UML diagram is best for object interactions over time?', 'Class diagram', 'Sequence diagram', 'Deployment diagram', 'Package diagram', 'B', 'Software Engineering'
    UNION ALL SELECT 'AT', 'DFA', 1, 'A DFA has how many transitions for each symbol from a state?', 'Zero or more', 'Exactly one', 'Exactly two', 'It depends on the alphabet order', 'B', 'Automata Theory'
    UNION ALL SELECT 'AT', 'NFA', 2, 'An NFA differs from a DFA because it may have what?', 'Negative states', 'Multiple possible next states', 'No accepting states', 'No alphabet', 'B', 'Automata Theory'
    UNION ALL SELECT 'AT', 'Regular Expressions', 3, 'Regular expressions are equivalent in power to which automata?', 'Pushdown automata', 'Turing machines', 'Finite automata', 'Context-free grammars only', 'C', 'Automata Theory'
    UNION ALL SELECT 'DBMS', 'Normalization', 1, 'Normalization primarily reduces what?', 'Internet speed', 'Data redundancy', 'Compiler warnings', 'Authentication roles', 'B', 'Database Management Systems (DBMS)'
    UNION ALL SELECT 'DBMS', 'Transactions', 2, 'Which ACID property ensures all-or-nothing execution?', 'Consistency', 'Isolation', 'Atomicity', 'Durability', 'C', 'Database Management Systems (DBMS)'
    UNION ALL SELECT 'DBMS', 'SQL Joins', 3, 'Which join returns matching rows from both tables only?', 'LEFT JOIN', 'RIGHT JOIN', 'INNER JOIN', 'FULL TEXT JOIN', 'C', 'Database Management Systems (DBMS)'
    UNION ALL SELECT 'DBMS', 'Normalization', 2, 'A table is in 2NF when it is in 1NF and has no what?', 'Foreign keys', 'Partial dependencies', 'Primary key', 'Indexes', 'B', 'Database Management Systems (DBMS)'
    UNION ALL SELECT 'AT', 'DFA', 2, 'A DFA for a binary language can be minimized to achieve what?', 'More variables', 'Fewer equivalent states', 'Larger alphabet', 'More epsilon transitions', 'B', 'Automata Theory'
    UNION ALL SELECT 'SE', 'Software Testing', 1, 'Regression testing is performed to verify what?', 'New features only', 'Existing features still work after changes', 'Database size', 'Compiler version', 'B', 'Software Engineering'
    UNION ALL SELECT 'OOPJ', 'Inheritance', 2, 'The keyword used to inherit a class in Java is?', 'implements', 'inherits', 'extends', 'instanceof', 'C', 'Object-Oriented Programming (Java)'
    UNION ALL SELECT 'OOPJ', 'Polymorphism', 3, 'Runtime polymorphism in Java is resolved using what?', 'Static typing only', 'Dynamic method dispatch', 'Package imports', 'Constructor chaining', 'B', 'Object-Oriented Programming (Java)'
    UNION ALL SELECT 'DBMS', 'SQL Joins', 2, 'Which clause is commonly used with JOIN to link tables?', 'ORDER BY', 'ON', 'LIMIT', 'GROUP BY', 'B', 'Database Management Systems (DBMS)'
    UNION ALL SELECT 'AT', 'Regular Expressions', 1, 'The union operator in regular expressions is commonly written as?', '|', '&', '*', '#', 'A', 'Automata Theory'
    UNION ALL SELECT 'SE', 'Requirements Engineering', 2, 'A use case mainly captures what?', 'Database normalization', 'User interactions with the system', 'CPU scheduling', 'Bytecode generation', 'B', 'Software Engineering'
    UNION ALL SELECT 'DBMS', 'Transactions', 3, 'Isolation in transactions helps prevent what class of issues?', 'Syntax errors', 'Concurrent access anomalies', 'Missing primary keys', 'Unused indexes', 'B', 'Database Management Systems (DBMS)'
) seed
JOIN subjects s ON s.code = seed.subject_code
JOIN topics t ON t.subject_id = s.id AND t.name = seed.topic_name
WHERE NOT EXISTS (
    SELECT 1
    FROM questions q
    WHERE q.question_text = seed.question_text
      AND q.subject_id = s.id
      AND q.topic_id = t.id
);
