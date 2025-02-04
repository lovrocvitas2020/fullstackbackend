package com.example.fullstackcrudreact.fullstackbackend.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorklogDTO {
    private Long id;
    private Long userId;
    private String username;  
    private LocalDate workDate;
    private LocalTime startHour;
    private LocalTime endHour;
    private long durationSeconds;  // Changed from Duration to long
    private String workDescription;

    // Constructor to convert Worklog to WorklogDTO
    public WorklogDTO(Worklog worklog) {
        this.id = worklog.getId();
        this.userId = worklog.getUser().getId();  
        this.username = worklog.getUser().getUsername();  
        this.workDate = worklog.getWorkDate();
        this.startHour = worklog.getStartHour();
        this.endHour = worklog.getEndHour();
        this.durationSeconds = worklog.getDuration().getSeconds();  // Convert Duration to seconds
        this.workDescription = worklog.getWorkDescription();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }

    public long getDurationSeconds() {  // Updated to use long instead of Duration
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {  // Updated setter
        this.durationSeconds = durationSeconds;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }
}
