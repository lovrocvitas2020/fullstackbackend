package com.example.fullstackcrudreact.fullstackbackend.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.exception.UserNotFoundException;
import com.example.fullstackcrudreact.fullstackbackend.model.LoginRequest;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;





@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

     @Autowired
    private PagedResourcesAssembler<User> pagedResourcesAssembler;

    /**
     * Creates new User
     * 
     * @param newUser
     * @return
     */
    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }


    @PostMapping("/loginuser")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    /**
     * Registers a new user
     * 
     * @param newUser
     * @return
     */
    @PostMapping("/register")
    public User registerUser(@RequestBody User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }
    
   /*
    * 
           @GetMapping("/users")
        List<User> getAllUsers(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue= "10") int size){
            logger.debug("Debug: UserController getAllUsers method");
        return  userRepository.findAll();
    }

    */

    @GetMapping("/users")
	public ResponseEntity<PagedModel<EntityModel<User>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
    Pageable paging = PageRequest.of(page, size);
    Page<User> userPage = userRepository.findAll(paging);
    PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(userPage, user -> 
        EntityModel.of(user,
                       WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(user.getId())).withSelfRel()));
    return ResponseEntity.ok(pagedModel);
	}

     
    /**
     * Gets User by id
     * 
     * @param id
     * @return
     */
    /*
      @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id) {
        logger.debug("Debug: UserController getUserById method");
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    
     */

     @GetMapping("/user/{id}")
     public ResponseEntity<EntityModel<User>> getUserById(@PathVariable Long id) {
         logger.debug("Debug: UserController getUserById method");
         User user = userRepository.findById(id)
                 .orElseThrow(() -> new UserNotFoundException(id));
         EntityModel<User> resource = EntityModel.of(user,
                 WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(id)).withSelfRel(),
                 WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers(0, 10)).withRel("users"));
         return ResponseEntity.ok(resource);
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
