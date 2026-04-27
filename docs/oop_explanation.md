# OOP Concepts Applied in Smart Adaptive Assessment Platform

This document explains how each Object-Oriented Programming (OOP) concept
is demonstrated in this project, with specific file and line references.

---

## 1. Encapsulation

**Definition:** Wrapping data (fields) and methods that operate on the data
into a single unit (class), restricting direct access to fields using
access modifiers (`private`), and providing controlled access through
`public` getters and setters.

### Where It's Applied:

**All Model classes** (User, Question, QuizSession, Attempt) use encapsulation:

```java
// File: model/User.java

// Private fields — data is HIDDEN
private int id;
private String username;
private String password;
private String email;

// Public getters/setters — CONTROLLED ACCESS
public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}
```

**Why it matters:** External code cannot directly modify `password` or other
sensitive fields. All access goes through methods, which could add validation.

---

## 2. Abstraction

**Definition:** Hiding complex implementation details and showing only the
essential features. Achieved through **abstract classes** and **interfaces**.

### Where It's Applied:

#### a) Interface — `AdaptiveStrategy`
```java
// File: strategy/AdaptiveStrategy.java

public interface AdaptiveStrategy {
    int adjustDifficulty(QuizSession session, boolean wasCorrect);
    String getStrategyName();
    int getMinDifficulty();
    int getMaxDifficulty();
}
```

The interface defines **WHAT** the adaptive system does, not **HOW**.
Any class implementing this interface must provide the logic.

#### b) Abstract Class — `BaseDAO<T>`
```java
// File: dao/BaseDAO.java

public abstract class BaseDAO<T> {
    public abstract T findById(int id) throws SQLException;
    public abstract List<T> findAll() throws SQLException;
    public abstract int insert(T entity) throws SQLException;
    public abstract boolean update(T entity) throws SQLException;
    public abstract boolean delete(int id) throws SQLException;

    // Concrete methods shared by all DAO subclasses
    protected Connection getConnection() throws SQLException { ... }
    protected void closeResources(...) { ... }
}
```

`BaseDAO` defines the contract for all data access operations while
providing shared helper methods.

---

## 3. Inheritance

**Definition:** A mechanism where a new class (child/subclass) inherits
properties and behaviors from an existing class (parent/superclass),
establishing an IS-A relationship.

### Where It's Applied:

#### a) `Admin extends User`
```java
// File: model/Admin.java

public class Admin extends User {
    private String department;

    public Admin() {
        super();            // Calls User's constructor
        setRole("ADMIN");   // Uses inherited method
    }
}
```

`Admin` IS-A `User` — it inherits all User fields and methods,
and adds admin-specific behavior.

#### b) All DAOs extend `BaseDAO`
```java
// File: dao/UserDAO.java
public class UserDAO extends BaseDAO<User> { ... }

// File: dao/QuestionDAO.java
public class QuestionDAO extends BaseDAO<Question> { ... }

// File: dao/QuizSessionDAO.java
public class QuizSessionDAO extends BaseDAO<QuizSession> { ... }

// File: dao/AttemptDAO.java
public class AttemptDAO extends BaseDAO<Attempt> { ... }
```

All concrete DAOs inherit shared database utility methods from `BaseDAO`
and must implement the abstract CRUD methods.

---

## 4. Polymorphism

**Definition:** The ability of objects to take many forms. A single
interface/method can work differently depending on the implementing class.

### Where It's Applied:

#### a) Interface Polymorphism — `AdaptiveStrategy`
```java
// File: service/QuizService.java

// Field declared with INTERFACE type (polymorphism)
private final AdaptiveStrategy adaptiveStrategy;

// Constructor uses concrete class, but field is interface type
public QuizService() {
    this.adaptiveStrategy = new RuleBasedAdaptive();  // Current implementation
}

// Can swap to a different strategy without changing QuizService
public QuizService(AdaptiveStrategy strategy) {
    this.adaptiveStrategy = strategy;  // ANY implementation works here
}

// Usage: The actual method called depends on which class was provided
adaptiveStrategy.adjustDifficulty(session, isCorrect);
```

The `QuizService` doesn't know or care which specific strategy is being used.
You could create a `MachineLearningAdaptive` or `RandomAdaptive` class
and plug it in without changing `QuizService`.

#### b) Method Overriding (Runtime Polymorphism)
Each DAO gives a different implementation of the same methods:
```java
// BaseDAO declares: public abstract T findById(int id)
// UserDAO implements:    → queries 'users' table
// QuestionDAO implements:→ queries 'questions' table
// Each behaves differently despite having the same method signature
```

---

## 5. Constructor Overloading

**Definition:** Having multiple constructors in a class, each with different
parameter lists, allowing objects to be created in different ways.

### Where It's Applied:

#### a) `User` class — 4 constructors
```java
// File: model/User.java

// 1. Default constructor
public User() { }

// 2. Constructor for login
public User(String username, String password) { }

// 3. Constructor for registration
public User(String username, String password, String email, String fullName) { }

// 4. Full constructor (from database)
public User(int id, String username, String password, String email,
            String fullName, String role, Timestamp createdAt) { }
```

#### b) `Question` class — 3 constructors
```java
// 1. Default constructor
public Question() { }

// 2. For creating new question (without ID)
public Question(String questionText, String optionA, ...) { }

// 3. Full constructor (from database)
public Question(int id, String questionText, String optionA, ...) { }
```

#### c) `QuizSession` class — 3 constructors
```java
// 1. Default constructor (sets defaults)
public QuizSession() { }

// 2. For starting a new session
public QuizSession(int userId, int totalQuestions) { }

// 3. Full constructor (from database)
public QuizSession(int id, int userId, int currentDifficulty, ...) { }
```

---

## 6. Method Overriding

**Definition:** A subclass provides its own implementation of a method that is
already defined in its parent class, changing the behavior.

### Where It's Applied:

#### a) `toString()` overriding in all Model classes
```java
// File: model/User.java
@Override
public String toString() {
    return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
}

// File: model/Admin.java — DIFFERENT implementation
@Override
public String toString() {
    return "Admin{id=" + getId() + ", username='" + getUsername()
            + "', department='" + department + "'}";
}
```

#### b) `isAdmin()` overriding
```java
// User.java
public boolean isAdmin() {
    return "ADMIN".equals(this.role);  // Checks the role field
}

// Admin.java — OVERRIDES parent's method
@Override
public boolean isAdmin() {
    return true;  // Admin is ALWAYS admin
}
```

#### c) Abstract method implementations in DAOs
```java
// BaseDAO declares abstract methods
// Each DAO subclass OVERRIDES them with concrete implementations:

// UserDAO
@Override
public User findById(int id) throws SQLException { ... }

// QuestionDAO
@Override
public Question findById(int id) throws SQLException { ... }
```

---

## Summary Table

| OOP Concept | Files Where Demonstrated |
|---|---|
| **Encapsulation** | `User.java`, `Question.java`, `QuizSession.java`, `Attempt.java` |
| **Abstraction** | `AdaptiveStrategy.java` (interface), `BaseDAO.java` (abstract class) |
| **Inheritance** | `Admin.java extends User`, `UserDAO extends BaseDAO`, all DAO classes |
| **Polymorphism** | `QuizService.java` (uses `AdaptiveStrategy` interface), DAO pattern |
| **Constructor Overloading** | `User.java` (4), `Question.java` (3), `QuizSession.java` (3), `Attempt.java` (3) |
| **Method Overriding** | `Admin.java` (toString, isAdmin), all DAO classes (CRUD methods) |

---

## Design Patterns Used

| Pattern | Where |
|---|---|
| **DAO Pattern** | `BaseDAO` + all concrete DAO classes |
| **Strategy Pattern** | `AdaptiveStrategy` + `RuleBasedAdaptive` |
| **MVC Pattern** | Controllers (Servlets) + Views (JSPs) + Models (POJOs) |
| **Layered Architecture** | Controller → Service → DAO → Database |
