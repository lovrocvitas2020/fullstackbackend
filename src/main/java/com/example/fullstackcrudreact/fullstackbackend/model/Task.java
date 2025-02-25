package com.example.fullstackcrudreact.fullstackbackend.model;

import java.sql.Timestamp;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Explicit ID strategy
    private Long id;

    private String taskName;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(nullable = false) // ✅ Ensures a due date is always present
    private Timestamp dueDate;

    @ManyToOne
    @JsonBackReference 
    @JoinColumn(name = "project_id", nullable = false) // ✅ Proper foreign key mapping
    private Project project;

    @Transient
    @JsonInclude
    private String projectName;

    @PrePersist
    protected void onCreate() {
        if (dueDate == null) {
            dueDate = Timestamp.from(Instant.now());
        }
    }

    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getDueDate() { return dueDate; }
    public void setDueDate(Timestamp dueDate) { this.dueDate = dueDate; }

    public Project getProject() { return project; }
    public void setProject(Project project) { 
        this.project = project; 
        this.projectName = project != null ? project.getProjectName() : null; // Set project name
    }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    @Override
    public String toString() {
        return "Task [id=" + id + ", taskName=" + taskName + ", description=" + description + ", status=" + status
                + ", dueDate=" + dueDate + ", project=" + project + "]";
    }
}
