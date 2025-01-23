package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.UserNotes;

public interface UserNotesRepository extends JpaRepository<UserNotes, Long>  {

}
