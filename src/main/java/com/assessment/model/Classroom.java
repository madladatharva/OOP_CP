package com.assessment.model;

import java.sql.Timestamp;

public class Classroom {

    private int id;
    private int teacherId;
    private String name;
    private String classCode;
    private String description;
    private int studentCount;
    private Timestamp createdAt;

    public Classroom() {
    }

    public Classroom(int id, int teacherId, String name, String classCode, String description,
            int studentCount, Timestamp createdAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.name = name;
        this.classCode = classCode;
        this.description = description;
        this.studentCount = studentCount;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
