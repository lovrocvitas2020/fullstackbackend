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
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.model.UserNotes;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserNotesRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;


@RestController
@CrossOrigin("http://localhost:3000")
public class UserNotesController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserNotesController.class);

    @Autowired
    private UserNotesRepository userNotesRepository;

    @Autowired
    private UserRepository userRepository;

    User fetchedUser = new User();

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

        System.out.println("UserNotesController getUserNoteById method userNotesRepository.findById(id).toString(): " +userNotesRepository.findById(id).toString());

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

      // Fetch the UserNotes by ID
    UserNotes fetchedUserNotes = userNotesRepository.findById(id)
    .orElseThrow(() -> new UserNoteNotFoundException(id));
        
        System.out.println("UserNotesController updateUserNote method: " + id);     
        System.out.println("UserNotesController updateUserNote newUserNotes.toString(): " + newUserNotes.toString());
        System.out.println("UserNotesController updateUserNote method userNotesRepository.findById(id).toString(): " +userNotesRepository.findById(id).toString());

       if(userNotesRepository.findById(id).isPresent()){
       
         
            fetchedUser = fetchedUserNotes.getUser();
            System.out.println("fetchedUser.getId(): "+fetchedUser.getId());
       } else {
             System.out.println("fetchedUser is null ");
       }


        Long userId = fetchedUser.getId();
        logger.debug("UserNotesController updateUserNote userId: " + userId);

            // Fetch the User from the userRepository
            User fetchedUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

       // Update the UserNotes
        fetchedUserNotes.setUsernote(newUserNotes.getUsernote());
        fetchedUserNotes.setUser(fetchedUser); // Ensure user is set
            
         // Save and return the updated UserNotes
    return userNotesRepository.save(fetchedUserNotes);
    }

}
