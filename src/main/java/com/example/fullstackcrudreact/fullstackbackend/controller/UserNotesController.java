package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.UserNotes;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserNotesRepository;


@RestController
@CrossOrigin("http://localhost:3000")
public class UserNotesController {

    @Autowired
    private UserNotesRepository userNotesRepository;


    // Add user note endpoint
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/add_user_notes")
    public UserNotes newUserNote(@RequestBody UserNotes newUserNotes){

        System.out.println("UserNotesController newUserNote method: "+newUserNotes.toString());

        return userNotesRepository.save(newUserNotes);
    }
    
   
     // Get all user notes
    @GetMapping("/user_notes")
    List<UserNotes> getAllUserNotes(){
        System.out.println("Debug: UserNotesController getAllUserNotes method");
        return userNotesRepository.findAll();
    }
    



}
