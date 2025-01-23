package com.example.fullstackcrudreact.fullstackbackend.model;

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
import jakarta.persistence.Table;

@Entity
@Table(name ="user_notes")
public class UserNotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String usernote;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id",
                foreignKey = @ForeignKey(name = "fk_user"))
    @JsonBackReference            
    private User user;

     // Constructors
     public UserNotes() {
    }

    public UserNotes(Long id, String usernote, User user) {
        this.id = id;
        this.usernote = usernote;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsernote() {
        return usernote;
    }

    public void setUsernote(String usernote) {
        this.usernote = usernote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // toString method
    @Override
    public String toString() {
        return "UserNotes{" +
                "id=" + id +
                ", usernote='" + usernote + '\'' +
                ", user=" + (user != null ? user.getId() : null) + // Avoid fetching the full user object
                '}';
    }


}
