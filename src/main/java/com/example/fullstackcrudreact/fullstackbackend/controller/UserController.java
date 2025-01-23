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
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;



@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates new User
     * 
     * @param newUser
     * @return
     */
    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
        return userRepository.save(newUser);
    }
    
    /**
     * Gets all Users
     * 
     * @return
     */
        @GetMapping("/users")
        List<User> getAllUsers(){
            logger.debug("Debug: UserController getAllUsers method");
        return  userRepository.findAll();
    }

    /**
     * Gets User by id
     * 
     * @param id
     * @return
     */
     @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id) {
        logger.debug("Debug: UserController getUserById method");
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Updates user
     * 
     * @param newUser
     * @param id
     * @return
     */
    @PutMapping("/user/{id}")
    User updateUser(@RequestBody User newUser, @PathVariable Long id) {
        logger.debug("Debug: UserController updateUser method");
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setName(newUser.getName());
                    user.setEmail(newUser.getEmail());
                    return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Deletes user
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/user/{id}")
    String deleteUser(@PathVariable Long id){
        logger.debug("Debug: UserController deleteUser method");
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        return  "User with id "+id+" has been deleted success.";
    }
    
    

}
