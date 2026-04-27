USE smart_assessment_db;

ALTER TABLE quiz_sessions
    ADD COLUMN IF NOT EXISTS selected_category VARCHAR(100) NULL AFTER total_questions;

ALTER TABLE questions
    MODIFY COLUMN category VARCHAR(100) NOT NULL DEFAULT 'General';

CREATE TABLE IF NOT EXISTS study_materials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    quiz_category VARCHAR(100) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_path VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    extracted_text LONGTEXT,
    generated_question_count INT NOT NULL DEFAULT 0,
    uploaded_by INT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'Which technology maps a URL to a Java class in this project?', 'JDBC', 'Servlet', 'JSP tag', 'JAR manifest', 'B', 1, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'Which technology maps a URL to a Java class in this project?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'Which object is used to keep a user logged in across requests?', 'PreparedStatement', 'HttpSession', 'ResultSet', 'ServletConfig', 'B', 1, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'Which object is used to keep a user logged in across requests?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What does JSP primarily help with in a Java web app?', 'Database pooling', 'Rendering dynamic HTML views', 'Compiling Java bytecode', 'Managing TCP sockets', 'B', 1, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What does JSP primarily help with in a Java web app?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What does JDBC stand for?', 'Java Database Connectivity', 'Java Distributed Component', 'JSON Database Connector', 'Java Data Container', 'A', 1, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What does JDBC stand for?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'Why is PreparedStatement preferred over building SQL with string concatenation?', 'It is always shorter', 'It prevents SQL injection and handles parameters safely', 'It avoids indexes', 'It skips database parsing', 'B', 2, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'Why is PreparedStatement preferred over building SQL with string concatenation?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What does a connection pool like HikariCP improve?', 'HTML rendering speed', 'Reuse of database connections', 'JSP compilation', 'Classpath scanning', 'B', 2, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What does a connection pool like HikariCP improve?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What is the main purpose of a servlet filter?', 'Store SQL schema', 'Intercept requests and responses for cross-cutting logic', 'Replace the database', 'Compile JSP files', 'B', 2, 'Java Web', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What is the main purpose of a servlet filter?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'Why must shared servlet resources be designed carefully?', 'Servlets run only once', 'Multiple requests can use the same servlet instance concurrently', 'Servlets cannot access sessions', 'Each servlet gets a new JVM', 'B', 2, 'Concurrency', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'Why must shared servlet resources be designed carefully?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What is the benefit of the synchronized keyword?', 'It formats strings', 'It controls concurrent access to critical sections', 'It creates new threads', 'It closes connections automatically', 'B', 3, 'Concurrency', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What is the benefit of the synchronized keyword?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What is a race condition?', 'A fast sorting algorithm', 'A bug caused by unpredictable timing between concurrent operations', 'A JDBC driver issue', 'A UI animation problem', 'B', 3, 'Concurrency', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What is a race condition?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'Why is immutable data often easier to use in concurrent systems?', 'It uses less memory in every case', 'It avoids accidental shared-state mutation between threads', 'It removes the need for classes', 'It makes SQL faster', 'B', 3, 'Concurrency', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'Why is immutable data often easier to use in concurrent systems?'
);

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by)
SELECT 'What problem does optimistic locking help detect?', 'Broken CSS links', 'Concurrent updates that overwrite each other', 'Slow page rendering', 'JAR version conflicts', 'B', 3, 'Concurrency', 1
WHERE NOT EXISTS (
    SELECT 1 FROM questions WHERE question_text = 'What problem does optimistic locking help detect?'
);
