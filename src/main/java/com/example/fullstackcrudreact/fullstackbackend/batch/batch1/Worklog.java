package com.example.fullstackcrudreact.fullstackbackend.batch.batch1;

import java.time.LocalDate;
import java.time.LocalTime;

public class Worklog {

    
    private Long id;
    private LocalTime startHour;
    private LocalTime endHour;
    private LocalDate workDate;
    private String workDescription;
    private Long userId;
    private Long hourSum;
    private String name;


    // Default constructor
    public Worklog() {}

    public Worklog(LocalTime startHour, LocalTime endHour, LocalDate workDate, 
                  String workDescription, Long userId, Long hourSum, String name) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.workDate = workDate;
        this.workDescription = workDescription;
        this.userId = userId;
        this.hourSum = hourSum;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalTime getStartHour() { return startHour; }
    public void setStartHour(LocalTime startHour) { this.startHour = startHour; }

    public LocalTime getEndHour() { return endHour; }
    public void setEndHour(LocalTime endHour) { this.endHour = endHour; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public String getWorkDescription() { return workDescription; }
    public void setWorkDescription(String workDescription) { 
        this.workDescription = workDescription; 
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getHourSum() { return hourSum; }
    public void setHourSum(Long hourSum) { this.hourSum = hourSum; }

    @Override
    public String toString() {
        return "Worklog{" +
                "id=" + id +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", workDate=" + workDate +
                ", workDescription='" + workDescription + '\'' +
                ", userId=" + userId +
                ", hourSum=" + hourSum +
                '}';
    }

}
