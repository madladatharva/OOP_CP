# Smart Adaptive Assessment Platform — Setup Guide

This repository currently runs the legacy single-quiz application on `sql/schema.sql`.
The classroom-platform redesign blueprint now lives in `docs/platform_redesign_v3.md`, and
the target normalized schema for that upgrade lives in `sql/schema_v3_classroom_platform.sql`.

## Prerequisites

Before setting up this project, make sure you have the following installed:

| Tool | Version | Purpose |
|------|---------|---------|
| **JDK** | 11 or higher | Java compiler and runtime |
| **Apache Maven** | 3.6+ | Build tool and dependency management |
| **MySQL** | 8.0+ | Database server |
| **Apache Tomcat** | 9.0+ | Web application server |
| **IDE** (optional) | IntelliJ IDEA / Eclipse / VS Code | Code editor |

---

## Step 1: Set Up the Database

### 1.1 Start MySQL Server
```bash
# macOS (Homebrew)
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows - Start from Services or MySQL Workbench
```

### 1.2 Create Database and Tables
Open a MySQL terminal or workbench and run:
```bash
mysql -u root -p < sql/schema.sql
```

Or, open the file `sql/schema.sql` in MySQL Workbench and execute it.

This will:
- Create the `smart_assessment_db` database
- Create the core tables including uploaded study materials
- Insert sample data including admin/user accounts and a larger question bank

If you already have an existing database from an older version, run:
```bash
mysql -u root -p smart_assessment_db < sql/migration_v2_pdf_quiz.sql
```

### 1.3 Verify Database
```sql
USE smart_assessment_db;
SHOW TABLES;
SELECT COUNT(*) FROM questions;
SELECT * FROM users;
```

---

## Step 2: Configure Database Connection

Edit the file:  
`src/main/java/com/assessment/util/DBConnection.java`

Update these constants if needed:
```java
private static final String URL = "jdbc:mysql://localhost:3306/smart_assessment_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USERNAME = "root";      // Your MySQL username
private static final String PASSWORD = "root";      // Your MySQL password
```

---

## Step 3: Build the Project

Navigate to the project root directory and run:

```bash
cd SmartAdaptiveAssessment
mvn clean package
```

This will:
- Download all dependencies (Servlet API, JSP API, JSTL, MySQL Connector)
- Compile all Java source files
- Package everything into a WAR file at `target/SmartAdaptiveAssessment.war`

---

## Step 4: Deploy to Tomcat

### Option A: Manual Deployment
1. Copy `target/SmartAdaptiveAssessment.war` to Tomcat's `webapps/` directory
2. Start Tomcat:
   ```bash
   # Navigate to Tomcat directory
   cd /path/to/apache-tomcat-9.x.x
   ./bin/startup.sh    # macOS/Linux
   bin\startup.bat     # Windows
   ```
3. Open browser: `http://localhost:8080/SmartAdaptiveAssessment/`

### Option B: IDE Deployment (IntelliJ IDEA)
1. Open the project in IntelliJ IDEA
2. Go to **Run → Edit Configurations**
3. Add a new **Tomcat Server → Local** configuration
4. Set the deployment artifact to `SmartAdaptiveAssessment:war exploded`
5. Click **Run**

### Option C: IDE Deployment (Eclipse)
1. Import as a Maven project
2. Right-click project → **Run As → Run on Server**
3. Select Apache Tomcat
4. Click **Finish**

---

## Step 5: Access the Application

### URLs:
| Page | URL |
|------|-----|
| Login | `http://localhost:8080/SmartAdaptiveAssessment/login` |
| Register | `http://localhost:8080/SmartAdaptiveAssessment/register` |
| User Dashboard | `http://localhost:8080/SmartAdaptiveAssessment/dashboard` |
| Admin Panel | `http://localhost:8080/SmartAdaptiveAssessment/admin/dashboard` |

### Sample Accounts:
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| User | `john_doe` | `user123` |
| User | `jane_smith` | `user123` |

---

## Step 6: Using the Application

### As a Regular User:
1. Login with credentials or register a new account
2. Click **Start Quiz** on the dashboard
3. Answer questions — difficulty adapts automatically
4. View results after completing the quiz
5. Check quiz history on the dashboard

### As an Admin:
1. Login with admin credentials
2. View question statistics on the admin dashboard
3. Add new questions via **Add New Question**
4. Upload a theory PDF and auto-generate a topic-based quiz from the admin panel
5. Edit or delete existing questions
6. Monitor platform through the dashboard

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **Database connection error** | Check MySQL is running and credentials in `DBConnection.java` match |
| **ClassNotFoundException for MySQL driver** | Ensure `mysql-connector-java` is in the WAR's `WEB-INF/lib/` |
| **404 on servlets** | Verify Tomcat version supports Servlet 4.0 annotations |
| **JSTL tags not rendering** | Ensure `jstl-1.2.jar` is in `WEB-INF/lib/` (Maven handles this) |
| **Build fails** | Run `mvn clean install -U` to force dependency refresh |

---

## Project Structure
```
SmartAdaptiveAssessment/
├── pom.xml                          # Maven build configuration
├── sql/
│   ├── schema.sql                   # Current runnable schema + sample data
│   └── schema_v3_classroom_platform.sql
├── docs/
│   ├── setup_guide.md               # This file
│   ├── platform_redesign_v3.md      # Teacher/student platform blueprint
│   └── oop_explanation.md           # OOP concepts explanation
└── src/main/
    ├── java/com/assessment/
    │   ├── model/                   # POJOs (User, Admin, Question, QuizSession, Attempt)
    │   ├── dao/                     # Data Access Objects (BaseDAO + concrete DAOs)
    │   ├── service/                 # Business Logic (UserService, QuestionService, QuizService)
    │   ├── controller/              # Servlets (Login, Register, Quiz, Result, Admin)
    │   ├── strategy/                # Adaptive Strategy (Interface + RuleBasedAdaptive)
    │   └── util/                    # Utilities (DBConnection)
    └── webapp/
        ├── WEB-INF/web.xml          # Deployment descriptor
        ├── css/style.css            # Stylesheet
        ├── login.jsp                # Login page
        ├── register.jsp             # Registration page
        ├── dashboard.jsp            # User dashboard
        ├── quiz.jsp                 # Quiz question page
        ├── feedback.jsp             # Answer feedback page
        ├── result.jsp               # Quiz results page
        ├── admin_dashboard.jsp      # Admin dashboard
        ├── admin_questions.jsp      # Manage questions
        ├── admin_add_question.jsp   # Add question form
        ├── admin_edit_question.jsp  # Edit question form
        └── error.jsp                # Error page
```
