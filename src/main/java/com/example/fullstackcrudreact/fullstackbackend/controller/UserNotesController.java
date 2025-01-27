package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.exception.UserNotFoundException;
import com.example.fullstackcrudreact.fullstackbackend.exception.UserNoteNotFoundException;
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

     /**
     * Gets User Note by id
     * 
     * @param id
     * @return
     */
    @GetMapping("/user_notes/{id}")
    public UserNotes getUserNoteById(@PathVariable Long id) {
        logger.debug("Debug: UserNotesController getUserNoteById method");
        return userNotesRepository.findById(id)
                .orElseThrow(() -> new UserNoteNotFoundException(id));
    }
    

    /**
     * Deletes User Note
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete_user_note/{id}")
    public String deleteUserNote(@PathVariable Long id) {
        logger.debug("UserNotesController deleteUserNote method: " + id);
        if (!userNotesRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userNotesRepository.deleteById(id);
        return "User note with id " + id + " has been deleted successfully.";
    }

     /**
     * Updates User Note
     *
     * @param newUserNotes
     * @param id
     * @return
     */
    @PutMapping("/update_user_note/{id}")
    public UserNotes updateUserNote(@RequestBody UserNotes newUserNotes, @PathVariable Long id) {
        logger.debug("UserNotesController updateUserNote method: " + id);
        return userNotesRepository.findById(id)
                .map(userNote -> {
                    userNote.setUsernote(newUserNotes.getUsernote());
                    userNote.setUser(newUserNotes.getUser());
                    return userNotesRepository.save(userNote);
                }).orElseThrow(() -> new UserNotFoundException(id));
    }

}
