package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
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

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserNotesController.class);

    @Autowired
    private UserNotesRepository userNotesRepository;


   /**
    *  Adds User Note

    * @param newUserNotes
    * @return
    */
   
    @PostMapping("/add_user_notes")
    public UserNotes newUserNote(@RequestBody UserNotes newUserNotes){

        logger.debug("UserNotesController newUserNote method: "+newUserNotes.toString());

        return userNotesRepository.save(newUserNotes);
    }
    
   
     /**
      *  Fetches all user_notes

      * @return
      */
    @GetMapping("/user_notes")
    List<UserNotes> getAllUserNotes(){
        logger.debug("UserNotesController getAllUserNotes method");
        return userNotesRepository.findAll();
    }
    



}
