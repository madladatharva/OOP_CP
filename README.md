# Smart Adaptive Assessment Platform

This project is a web-based adaptive assessment platform built with Java, Servlets, and JSP. It demonstrates key Object-Oriented Programming (OOP) principles and provides a functional system for students and administrators to manage and take quizzes. The platform dynamically adjusts the difficulty of questions based on user performance.

## Features

-   **User Roles:** Separate interfaces and functionality for Students and Admins.
-   **Secure Authentication:** User registration and login with password hashing (jBCrypt).
-   **Adaptive Quizzes:** The quiz difficulty adapts to the student's performance in real-time.
-   **Question Bank:** Admins can perform full CRUD (Create, Read, Update, Delete) operations on the question bank.
-   **Quiz History:** Students can view their past quiz attempts and results.
-   **PDF to Quiz:** Admins can upload a PDF, and the system will automatically generate a quiz from its content.
-   **Centralized Database:** All data is stored in a MySQL database.
-   **Connection Pooling:** Efficient database connection management using HikariCP.

## Technologies Used

-   **Backend:** Java 11, Java Servlets
-   **Frontend:** JSP (JavaServer Pages), JSTL, HTML, CSS
-   **Database:** MySQL
-   **Build Tool:** Apache Maven
-   **Web Server:** Apache Tomcat (via `tomcat7-maven-plugin`)
-   **Libraries:**
    -   `jbcrypt`: For secure password hashing.
    -   `HikariCP`: For high-performance JDBC connection pooling.
    -   `mysql-connector-java`: JDBC driver for MySQL.
    -   `org.apache.pdfbox`: For extracting text from PDF documents.
    -   `com.google.code.gson`: For handling JSON data.

## Setup and Run

Follow these steps to get the project running on your local machine.

### 1. Prerequisites

-   **Java JDK 11** or later installed.
-   **Apache Maven** installed.
-   **MySQL** database server installed and running.
-   **Git** for cloning the repository.

### 2. Database Setup

1.  Start your MySQL server.
2.  Log in to your MySQL client (e.g., `mysql -u root -p`).
3.  Create a new database for the project:
    ```sql
    CREATE DATABASE smart_assessment_db;
    ```
4.  Switch to the new database:
    ```sql
    USE smart_assessment_db;
    ```
5.  Run the schema script to create the necessary tables. You can find this file at `sql/schema.sql`. Execute the contents of this file in your MySQL client.

### 3. Application Configuration

The database connection settings are located in `src/main/java/com/assessment/util/DBConnection.java`. If your MySQL username or password are not `root`/`root`, you will need to update them there.

### 4. Build and Run

1.  Open your terminal and navigate to the project's root directory (`SmartAdaptiveAssessment`).
2.  Clean the project and install dependencies using Maven:
    ```bash
    mvn clean install
    ```
3.  Run the application using the Tomcat 7 Maven plugin:
    ```bash
    mvn tomcat7:run
    ```
4.  The application will be accessible at **[http://localhost:8080/SmartAdaptiveAssessment/](http://localhost:8080/SmartAdaptiveAssessment/)**.

## Default Login Credentials

You can use the following pre-seeded accounts to log in:

-   **Admin:**
    -   **Username:** `admin`
    -   **Password:** `admin123`
-   **Student:**
    -   **Username:** `student`
    -   **Password:** `student123`
