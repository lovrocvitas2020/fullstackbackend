package com.example.fullstackcrudreact.fullstackbackend.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorklogDTO {
    private Long id;
    private Long userId;
    private String username;  // To store the username
    private LocalDate workDate;
    private LocalTime startHour;
    private LocalTime endHour;
    private String workDescription;

    // Constructor to convert Worklog to WorklogDTO
    public WorklogDTO(Worklog worklog) {
        this.id = worklog.getId();
        this.userId = worklog.getUser().getId();  // Get the user id
        this.username = worklog.getUser().getUsername();  // Get the username
        this.workDate = worklog.getWorkDate();
        this.startHour = worklog.getStartHour();
        this.endHour = worklog.getEndHour();
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

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }
}
