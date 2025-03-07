package com.example.fullstackcrudreact.fullstackbackend.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Worklog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id",
                foreignKey = @ForeignKey(name = "fk_worklog_user"))
    @JsonBackReference
    private User user;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @Column(name = "hour_sum", nullable = false)
    private long durationSeconds; // Renamed for clarity

    @Column(name = "work_description", length = 500, nullable = false)
    private String workDescription;

    public Worklog() {
    }

    public Worklog(User user, LocalDate workDate, LocalTime startHour, LocalTime endHour, String workDescription) {
        this.user = user;
        this.workDate = workDate;
        this.startHour = startHour;
        this.endHour = endHour;
        this.workDescription = workDescription;
        calculateDuration(); // Ensure duration is set
    }

    // Ensure duration is calculated before persisting or updating
    @PrePersist
    @PreUpdate
    private void calculateDuration() {
        if (startHour != null && endHour != null) {
            durationSeconds = Duration.between(startHour, endHour).getSeconds();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        calculateDuration(); // Recalculate on change
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
        calculateDuration(); // Recalculate on change
    }

    public Duration getDuration() {
        return Duration.ofSeconds(durationSeconds);
    }

    public void setDuration(Duration duration) {
        this.durationSeconds = duration.getSeconds();
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    @Override
    public String toString() {
        return "WorkLog{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +  // Avoid fetching full user object
                ", workDate=" + workDate +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", durationSeconds=" + durationSeconds +
                ", workDescription='" + workDescription + '\'' +
                '}';
    }
}
