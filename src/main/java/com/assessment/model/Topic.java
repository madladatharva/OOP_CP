package com.assessment.model;

public class Topic {

    private int id;
    private int subjectId;
    private String subjectName;
    private String name;

    public Topic() {
    }

    public Topic(int id, int subjectId, String subjectName, String name) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
