-- ============================================================
-- Smart Adaptive Assessment Platform - Database Schema
-- Database: smart_assessment_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_assessment_db;
USE smart_assessment_db;

-- ============================================================
-- Table: users
-- Stores both regular users and admins (role-based)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Table: questions
-- Stores quiz questions with 4 options and difficulty levels
-- difficulty_level: 1 = Easy, 2 = Medium, 3 = Hard
-- ============================================================
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    difficulty_level INT NOT NULL DEFAULT 2,
    category VARCHAR(100) NOT NULL DEFAULT 'General',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CHECK (difficulty_level BETWEEN 1 AND 3),
    CHECK (correct_option IN ('A', 'B', 'C', 'D'))
) ENGINE=InnoDB;

-- ============================================================
-- Table: quiz_sessions
-- Tracks each quiz attempt by a user
-- status: ACTIVE, COMPLETED, ABANDONED
-- ============================================================
CREATE TABLE IF NOT EXISTS quiz_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    current_difficulty INT NOT NULL DEFAULT 2,
    total_questions INT NOT NULL DEFAULT 10,
    selected_category VARCHAR(100) NULL,
    current_question_number INT NOT NULL DEFAULT 0,
    score INT NOT NULL DEFAULT 0,
    consecutive_correct INT NOT NULL DEFAULT 0,
    consecutive_wrong INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'COMPLETED', 'ABANDONED') NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: attempts
-- Records each individual question attempt within a session
-- ============================================================
CREATE TABLE IF NOT EXISTS attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option CHAR(1) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    difficulty_at_time INT NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES quiz_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CHECK (selected_option IN ('A', 'B', 'C', 'D'))
) ENGINE=InnoDB;

-- ============================================================
-- Table: study_materials
-- Stores uploaded theory PDFs and the quiz topic generated from them
-- ============================================================
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

-- ============================================================
-- Sample Data: Admin User (password: admin123)
-- ============================================================
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', 'admin123', 'admin@assessment.com', 'System Administrator', 'ADMIN');

-- ============================================================
-- Sample Data: Regular User (password: user123)
-- ============================================================
INSERT INTO users (username, password, email, full_name, role) VALUES
('john_doe', 'user123', 'john@example.com', 'John Doe', 'USER'),
('jane_smith', 'user123', 'jane@example.com', 'Jane Smith', 'USER');

-- ============================================================
-- Sample Data: Questions
-- Difficulty: 1 = Easy, 2 = Medium, 3 = Hard
-- ============================================================

-- ---- EASY QUESTIONS (difficulty_level = 1) ----
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What does OOP stand for?', 'Object Oriented Programming', 'Object Ordered Processing', 'Open Object Protocol', 'Ordered Object Programming', 'A', 1, 'Java Basics', 1),
('Which keyword is used to create an object in Java?', 'create', 'new', 'object', 'make', 'B', 1, 'Java Basics', 1),
('What is the default value of an int variable in Java?', '1', 'null', '0', 'undefined', 'C', 1, 'Java Basics', 1),
('Which of these is a valid Java data type?', 'integer', 'floating', 'double', 'decimal', 'C', 1, 'Java Basics', 1),
('What is the extension of a Java source file?', '.class', '.java', '.js', '.jv', 'B', 1, 'Java Basics', 1),
('Which method is the entry point of a Java program?', 'start()', 'run()', 'main()', 'init()', 'C', 1, 'Java Basics', 1),
('What does JVM stand for?', 'Java Virtual Machine', 'Java Variable Method', 'Java Verified Module', 'Java Visual Manager', 'A', 1, 'Java Basics', 1),
('Which symbol is used for single-line comments in Java?', '/* */', '#', '//', '--', 'C', 1, 'Java Basics', 1);

-- ---- MEDIUM QUESTIONS (difficulty_level = 2) ----
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('Which OOP concept hides internal details?', 'Inheritance', 'Polymorphism', 'Encapsulation', 'Abstraction', 'C', 2, 'OOP Concepts', 1),
('What is method overloading?', 'Same method name, different parameters', 'Same method in child class', 'Calling parent method', 'Using interfaces', 'A', 2, 'OOP Concepts', 1),
('Which keyword is used to inherit a class?', 'implements', 'extends', 'inherits', 'super', 'B', 2, 'OOP Concepts', 1),
('What is an abstract class?', 'A class that cannot be modified', 'A class that cannot be instantiated', 'A class with only static methods', 'A class with no methods', 'B', 2, 'OOP Concepts', 1),
('What is the purpose of the ''this'' keyword?', 'To call parent class', 'To create new object', 'To refer to current object', 'To define static context', 'C', 2, 'OOP Concepts', 1),
('Which access modifier makes a member accessible only within its class?', 'public', 'protected', 'default', 'private', 'D', 2, 'OOP Concepts', 1),
('What is constructor overloading?', 'Multiple constructors with different parameters', 'Calling one constructor from another', 'Overriding parent constructor', 'Using default constructor', 'A', 2, 'OOP Concepts', 1),
('What does the ''super'' keyword refer to?', 'Current class', 'Parent class', 'Child class', 'Static context', 'B', 2, 'OOP Concepts', 1);

-- ---- HARD QUESTIONS (difficulty_level = 3) ----
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('Which design pattern uses a single instance of a class?', 'Factory', 'Observer', 'Singleton', 'Strategy', 'C', 3, 'Advanced Java', 1),
('What is the diamond problem in Java?', 'Issues with multiple inheritance of classes', 'Null pointer in diamond-shaped data', 'Circular dependency in packages', 'Memory leak in nested loops', 'A', 3, 'Advanced Java', 1),
('What is the difference between ''=='' and ''.equals()'' for objects?', 'No difference', '== checks reference, .equals() checks value', '== checks value, .equals() checks reference', '== is faster', 'B', 3, 'Advanced Java', 1),
('What is a functional interface in Java?', 'An interface with only default methods', 'An interface with exactly one abstract method', 'An interface with no methods', 'An interface with static methods only', 'B', 3, 'Advanced Java', 1),
('Which collection does not allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'Vector', 'C', 3, 'Advanced Java', 1),
('What is the DAO design pattern?', 'Data Access Object - separates data logic from business logic', 'Dynamic Array Object - manages arrays', 'Direct API Object - handles API calls', 'Database Admin Object - manages DB users', 'A', 3, 'Advanced Java', 1),
('What exception is thrown when accessing a null reference?', 'IOException', 'ArrayIndexOutOfBoundsException', 'NullPointerException', 'ClassNotFoundException', 'C', 3, 'Advanced Java', 1),
('What is the purpose of the ''final'' keyword on a method?', 'Method runs last', 'Method cannot be overridden', 'Method is static', 'Method is abstract', 'B', 3, 'Advanced Java', 1);

-- ============================================================
-- Additional Questions: Data Structures
-- ============================================================

-- EASY Data Structures
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is an array?', 'A collection of elements of the same type', 'A single variable', 'A type of loop', 'A function', 'A', 1, 'Data Structures', 1),
('Which data structure follows FIFO principle?', 'Stack', 'Queue', 'Tree', 'Graph', 'B', 1, 'Data Structures', 1),
('Which data structure follows LIFO principle?', 'Queue', 'Array', 'Stack', 'Linked List', 'C', 1, 'Data Structures', 1),
('What is the index of the first element in an array?', '1', '0', '-1', 'Depends on language', 'B', 1, 'Data Structures', 1),
('What is a linked list?', 'An array with links', 'A sequence of nodes connected by pointers', 'A type of tree', 'A circular array', 'B', 1, 'Data Structures', 1);

-- MEDIUM Data Structures
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is the time complexity of accessing an element in an array by index?', 'O(n)', 'O(log n)', 'O(1)', 'O(n^2)', 'C', 2, 'Data Structures', 1),
('Which data structure is used in BFS traversal?', 'Stack', 'Queue', 'Priority Queue', 'Deque', 'B', 2, 'Data Structures', 1),
('What is a binary search tree?', 'A tree with exactly two nodes', 'A tree where left child < parent < right child', 'Any tree with two branches', 'A balanced tree', 'B', 2, 'Data Structures', 1),
('Which data structure uses a hash function?', 'Array', 'Linked List', 'HashMap', 'Stack', 'C', 2, 'Data Structures', 1),
('What is the worst-case time complexity of inserting at the beginning of an ArrayList?', 'O(1)', 'O(log n)', 'O(n)', 'O(n^2)', 'C', 2, 'Data Structures', 1);

-- HARD Data Structures
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is the amortized time complexity of adding to a dynamic array?', 'O(n)', 'O(log n)', 'O(1)', 'O(n log n)', 'C', 3, 'Data Structures', 1),
('What is a red-black tree?', 'A binary tree with colored nodes', 'A self-balancing BST with node coloring rules', 'A tree used for sorting', 'A graph with colored edges', 'B', 3, 'Data Structures', 1),
('What is the space complexity of a recursive DFS on a graph with V vertices?', 'O(1)', 'O(V)', 'O(V^2)', 'O(log V)', 'B', 3, 'Data Structures', 1),
('In a min-heap, which property holds true?', 'Parent >= children', 'Parent <= children', 'Left child < right child', 'All leaves are equal', 'B', 3, 'Data Structures', 1),
('What is the time complexity of searching in a balanced BST?', 'O(n)', 'O(1)', 'O(log n)', 'O(n log n)', 'C', 3, 'Data Structures', 1);

-- ============================================================
-- Additional Questions: Algorithms
-- ============================================================

-- EASY Algorithms
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is the purpose of a sorting algorithm?', 'To search elements', 'To arrange elements in order', 'To delete elements', 'To compress data', 'B', 1, 'Algorithms', 1),
('Which sorting algorithm repeatedly swaps adjacent elements?', 'Merge Sort', 'Quick Sort', 'Bubble Sort', 'Selection Sort', 'C', 1, 'Algorithms', 1),
('What does Big O notation describe?', 'The exact runtime', 'The worst-case growth rate', 'Memory usage', 'Code quality', 'B', 1, 'Algorithms', 1),
('What is a linear search?', 'Searching by dividing in half', 'Checking each element one by one', 'Using a hash table', 'Sorting then searching', 'B', 1, 'Algorithms', 1);

-- MEDIUM Algorithms
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is the time complexity of binary search?', 'O(n)', 'O(n^2)', 'O(log n)', 'O(1)', 'C', 2, 'Algorithms', 1),
('Which algorithm technique solves problems by breaking them into smaller subproblems?', 'Greedy', 'Divide and Conquer', 'Brute Force', 'Backtracking', 'B', 2, 'Algorithms', 1),
('What is the best-case time complexity of Quick Sort?', 'O(n^2)', 'O(n)', 'O(n log n)', 'O(log n)', 'C', 2, 'Algorithms', 1),
('Which sorting algorithm is stable and has O(n log n) complexity?', 'Quick Sort', 'Heap Sort', 'Merge Sort', 'Selection Sort', 'C', 2, 'Algorithms', 1),
('What is memoization?', 'A way to write comments', 'Caching results of expensive function calls', 'A sorting technique', 'Memory management', 'B', 2, 'Algorithms', 1);

-- HARD Algorithms
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is the time complexity of Dijkstra''s algorithm with a min-heap?', 'O(V^2)', 'O(V + E)', 'O((V + E) log V)', 'O(V * E)', 'C', 3, 'Algorithms', 1),
('Which problem is NP-Complete?', 'Binary Search', 'Merge Sort', 'Travelling Salesman Problem', 'BFS Traversal', 'C', 3, 'Algorithms', 1),
('What is the master theorem used for?', 'Proving NP-completeness', 'Analyzing recursive algorithms', 'Sorting arrays', 'Graph coloring', 'B', 3, 'Algorithms', 1),
('What is the time complexity of building a heap from an array?', 'O(n log n)', 'O(n^2)', 'O(n)', 'O(log n)', 'C', 3, 'Algorithms', 1);

-- ============================================================
-- Additional Questions: Python
-- ============================================================

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('Which keyword is used to define a function in Python?', 'func', 'function', 'def', 'define', 'C', 1, 'Python', 1),
('What is a list in Python?', 'An immutable sequence', 'A mutable ordered collection', 'A key-value store', 'A set of unique items', 'B', 1, 'Python', 1),
('How do you add an element to a list in Python?', 'list.add()', 'list.push()', 'list.append()', 'list.insert()', 'C', 1, 'Python', 1),
('What is a dictionary in Python?', 'An ordered list', 'A key-value pair collection', 'A sorted array', 'A linked list', 'B', 2, 'Python', 1),
('What is a list comprehension?', 'A way to sort lists', 'A concise way to create lists', 'A type of loop', 'A search algorithm', 'B', 2, 'Python', 1),
('What is a decorator in Python?', 'A design pattern', 'A function that modifies another function', 'A class method', 'A variable type', 'B', 3, 'Python', 1),
('What is the GIL in Python?', 'Global Import Library', 'Global Interpreter Lock', 'General Interface Layer', 'Graphic Integration Library', 'B', 3, 'Python', 1);

-- ============================================================
-- Additional Questions: SQL & Databases
-- ============================================================

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What does SQL stand for?', 'Structured Query Language', 'Simple Query Logic', 'System Query Language', 'Standard Query List', 'A', 1, 'SQL & Databases', 1),
('Which SQL keyword is used to retrieve data?', 'GET', 'FETCH', 'SELECT', 'RETRIEVE', 'C', 1, 'SQL & Databases', 1),
('What is a PRIMARY KEY?', 'Any column in a table', 'A unique identifier for each row', 'A foreign reference', 'A data type', 'B', 1, 'SQL & Databases', 1),
('What is a JOIN in SQL?', 'Merging two databases', 'Combining rows from two or more tables', 'Adding a new column', 'Deleting duplicate rows', 'B', 2, 'SQL & Databases', 1),
('What is normalization in databases?', 'Making data bigger', 'Organizing data to reduce redundancy', 'Encrypting data', 'Compressing tables', 'B', 2, 'SQL & Databases', 1),
('What is an ACID transaction?', 'A type of query', 'Properties ensuring reliable database transactions', 'A backup method', 'A NoSQL concept', 'B', 3, 'SQL & Databases', 1),
('What is the difference between HAVING and WHERE?', 'No difference', 'HAVING filters groups, WHERE filters rows', 'WHERE filters groups, HAVING filters rows', 'HAVING is faster', 'B', 3, 'SQL & Databases', 1);

-- ============================================================
-- Additional Questions: Operating Systems
-- ============================================================

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What is an operating system?', 'A programming language', 'Software that manages hardware and software resources', 'A web browser', 'A database system', 'B', 1, 'Operating Systems', 1),
('What is a process?', 'A file on disk', 'A program in execution', 'A type of memory', 'A hardware component', 'B', 1, 'Operating Systems', 1),
('What is virtual memory?', 'RAM', 'An illusion of larger memory using disk space', 'Cache memory', 'ROM', 'B', 2, 'Operating Systems', 1),
('What is a deadlock?', 'A system crash', 'A state where processes wait for each other indefinitely', 'A memory leak', 'A network failure', 'B', 2, 'Operating Systems', 1),
('What is thrashing in OS?', 'Excessive paging causing performance drop', 'Deleting files', 'CPU overheating', 'Network congestion', 'A', 3, 'Operating Systems', 1),
('What is the Banker''s Algorithm used for?', 'Memory allocation', 'Deadlock avoidance', 'Process scheduling', 'File management', 'B', 3, 'Operating Systems', 1);

-- ============================================================
-- Additional Questions: Networking
-- ============================================================

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('What does HTTP stand for?', 'HyperText Transfer Protocol', 'High Tech Transfer Protocol', 'HyperText Transmission Process', 'High Transfer Text Protocol', 'A', 1, 'Networking', 1),
('What is an IP address?', 'A website name', 'A unique identifier for a device on a network', 'A type of cable', 'A programming language', 'B', 1, 'Networking', 1),
('What is TCP?', 'A programming language', 'A connection-oriented transport protocol', 'A type of cable', 'A database', 'B', 2, 'Networking', 1),
('What is the difference between TCP and UDP?', 'TCP is wireless, UDP is wired', 'TCP is reliable and ordered, UDP is faster but unreliable', 'No difference', 'UDP is more secure', 'B', 2, 'Networking', 1),
('What is DNS?', 'Data Network Service', 'Domain Name System', 'Digital Network Security', 'Dynamic Node Switching', 'B', 2, 'Networking', 1),
('What is the OSI model?', 'A programming framework', 'A 7-layer network communication model', 'An operating system', 'A database schema', 'B', 3, 'Networking', 1);

-- ============================================================
-- Additional Questions: Java Enterprise & Concurrency
-- ============================================================

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty_level, category, created_by) VALUES
('Which technology maps a URL to a Java class in this project?', 'JDBC', 'Servlet', 'JSP tag', 'JAR manifest', 'B', 1, 'Java Web', 1),
('Which object is used to keep a user logged in across requests?', 'PreparedStatement', 'HttpSession', 'ResultSet', 'ServletConfig', 'B', 1, 'Java Web', 1),
('What does JSP primarily help with in a Java web app?', 'Database pooling', 'Rendering dynamic HTML views', 'Compiling Java bytecode', 'Managing TCP sockets', 'B', 1, 'Java Web', 1),
('What does JDBC stand for?', 'Java Database Connectivity', 'Java Distributed Component', 'JSON Database Connector', 'Java Data Container', 'A', 1, 'Java Web', 1),
('Why is PreparedStatement preferred over building SQL with string concatenation?', 'It is always shorter', 'It prevents SQL injection and handles parameters safely', 'It avoids indexes', 'It skips database parsing', 'B', 2, 'Java Web', 1),
('What does a connection pool like HikariCP improve?', 'HTML rendering speed', 'Reuse of database connections', 'JSP compilation', 'Classpath scanning', 'B', 2, 'Java Web', 1),
('What is the main purpose of a servlet filter?', 'Store SQL schema', 'Intercept requests and responses for cross-cutting logic', 'Replace the database', 'Compile JSP files', 'B', 2, 'Java Web', 1),
('Why must shared servlet resources be designed carefully?', 'Servlets run only once', 'Multiple requests can use the same servlet instance concurrently', 'Servlets cannot access sessions', 'Each servlet gets a new JVM', 'B', 2, 'Concurrency', 1),
('What is the benefit of the synchronized keyword?', 'It formats strings', 'It controls concurrent access to critical sections', 'It creates new threads', 'It closes connections automatically', 'B', 3, 'Concurrency', 1),
('What is a race condition?', 'A fast sorting algorithm', 'A bug caused by unpredictable timing between concurrent operations', 'A JDBC driver issue', 'A UI animation problem', 'B', 3, 'Concurrency', 1),
('Why is immutable data often easier to use in concurrent systems?', 'It uses less memory in every case', 'It avoids accidental shared-state mutation between threads', 'It removes the need for classes', 'It makes SQL faster', 'B', 3, 'Concurrency', 1),
('What problem does optimistic locking help detect?', 'Broken CSS links', 'Concurrent updates that overwrite each other', 'Slow page rendering', 'JAR version conflicts', 'B', 3, 'Concurrency', 1);
