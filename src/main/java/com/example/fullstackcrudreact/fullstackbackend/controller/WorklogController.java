package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.fullstackcrudreact.fullstackbackend.model.Worklog;
import com.example.fullstackcrudreact.fullstackbackend.model.WorklogDTO;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.WorklogRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class WorklogController {

    @Autowired
    private WorklogRepository worklogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     *  Get all worklogs
     * 
     * @return
     */
    @GetMapping("/viewworklog")
    public List<WorklogDTO> getAllWorklogs() {
    List<Worklog> worklogs = worklogRepository.findAll();
    List<WorklogDTO> worklogDTOs = worklogs.stream()
            .map(WorklogDTO::new)  // Convert each Worklog to WorklogDTO
            .collect(Collectors.toList());
    return worklogDTOs;
}

    /**
     *  Get one worklog
     * 
     * @param id
     * @return
     */
    @GetMapping("/worklog/{id}")
    public ResponseEntity<Worklog> getWorklogById(@PathVariable Long id) {
        Optional<Worklog> worklog = worklogRepository.findById(id);
        return worklog.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     *  Add worklog
     * 
     * @param worklog
     * @return
     */
    @PostMapping("/add_worklog")
    public ResponseEntity<Worklog> createWorklog(@RequestBody Map<String, Object> worklogMap) {
        // Extract data from the map
        Long userId = ((Number) worklogMap.get("user")).longValue();
        String workDate = (String) worklogMap.get("workDate");
        String startHour = (String) worklogMap.get("startHour");
        String endHour = (String) worklogMap.get("endHour");
        String workDescription = (String) worklogMap.get("workDescription");

        System.out.println("createWorklog userId: "+userId);
        System.out.println("createWorklog workDate: "+workDate);
        System.out.println("createWorklog startHour: "+startHour);
        System.out.println("createWorklog endHour: "+endHour);
        System.out.println("createWorklog workDescription: "+workDescription);

         User fetchedUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

         System.out.println("createWorklog fetchedUser:"+fetchedUser);   

       
        // Create and save the Worklog entity
      Worklog worklog = new Worklog(fetchedUser, LocalDate.parse(workDate), LocalTime.parse(startHour), LocalTime.parse(endHour), workDescription);

      System.out.println("createWorklog worklog.toString(): "+worklog.toString());

      Worklog createdWorklog = worklogRepository.save(worklog);

        
       return new ResponseEntity<>(createdWorklog, HttpStatus.CREATED);
     //   return null;

    }

    /**
     *  Update worklog
     * 
     * @param id
     * @param worklogDetails
     * @return
     */
    @PutMapping("/update_worklog/{id}")
    public ResponseEntity<Worklog> updateWorklog(@PathVariable Long id, @RequestBody Worklog worklogDetails) {
        Optional<Worklog> worklog = worklogRepository.findById(id);

        
        System.out.println("updateWorklog START ");
        System.out.println("worklogDetails.toString "+worklogDetails.toString());

        System.out.println("worklog.toString(): "+worklog.toString());
        System.out.println("worklog.toString(): ");

        if (worklog.isPresent()) {
          
          
            System.out.println("User worklog.get().getUser() : " + worklog.get().getUser());
        } else {
            System.out.println("WorkLog is not present");
        }
       

        User fetchedUser = worklog.get().getUser();
        System.out.println("fetchedUser: "+fetchedUser);


        if (worklog.isPresent()) {
            Worklog updatedWorklog = worklog.get();
            updatedWorklog.setUser(worklog.get().getUser()); 
            updatedWorklog.setWorkDate(worklogDetails.getWorkDate());
            updatedWorklog.setStartHour(worklogDetails.getStartHour());
            updatedWorklog.setEndHour(worklogDetails.getEndHour());
            updatedWorklog.setWorkDescription(worklogDetails.getWorkDescription());
            worklogRepository.save(updatedWorklog);
            return new ResponseEntity<>(updatedWorklog, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete_worklog/{id}")
    public ResponseEntity<HttpStatus> deleteWorklog(@PathVariable Long id) {
        Optional<Worklog> worklog = worklogRepository.findById(id);
        if (worklog.isPresent()) {
            worklogRepository.delete(worklog.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
