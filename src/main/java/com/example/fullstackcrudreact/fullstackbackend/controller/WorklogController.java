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
     * Get all worklogs
     */
    @GetMapping("/viewworklog")
    public List<WorklogDTO> getAllWorklogs() {
        List<Worklog> worklogs = worklogRepository.findAll();
        return worklogs.stream()
                .map(WorklogDTO::new)  // Convert each Worklog to WorklogDTO
                .collect(Collectors.toList());
    }

    /**
     * Get one worklog
     */
    @GetMapping("/worklog/{id}")
    public ResponseEntity<WorklogDTO> getWorklogById(@PathVariable Long id) {
        Optional<Worklog> worklog = worklogRepository.findById(id);
        return worklog.map(w -> new ResponseEntity<>(new WorklogDTO(w), HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Add worklog
     */
    @PostMapping("/add_worklog")
    public ResponseEntity<WorklogDTO> createWorklog(@RequestBody Map<String, Object> worklogMap) {
        try {
            Long userId = ((Number) worklogMap.get("user")).longValue();
            String workDate = (String) worklogMap.get("workDate");
            String startHour = (String) worklogMap.get("startHour");
            String endHour = (String) worklogMap.get("endHour");
            String workDescription = (String) worklogMap.get("workDescription");

            LocalTime start = LocalTime.parse(startHour);
            LocalTime end = LocalTime.parse(endHour);

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("End time must be after start time.");
            }

            User fetchedUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Worklog worklog = new Worklog(fetchedUser, LocalDate.parse(workDate), start, end, workDescription);
            Worklog createdWorklog = worklogRepository.save(worklog);

            return new ResponseEntity<>(new WorklogDTO(createdWorklog), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update worklog
     */
    @PutMapping("/update_worklog/{id}")
    public ResponseEntity<WorklogDTO> updateWorklog(@PathVariable Long id, @RequestBody Worklog worklogDetails) {
        Optional<Worklog> worklogOptional = worklogRepository.findById(id);

        if (worklogOptional.isPresent()) {
            Worklog worklog = worklogOptional.get();

            // Validate times
            LocalTime startHour = worklogDetails.getStartHour();
            LocalTime endHour = worklogDetails.getEndHour();

            if (endHour.isBefore(startHour)) {
                throw new IllegalArgumentException("End time must be after start time.");
            }

            worklog.setWorkDate(worklogDetails.getWorkDate());
            worklog.setStartHour(startHour);
            worklog.setEndHour(endHour);
            worklog.setWorkDescription(worklogDetails.getWorkDescription());

            // Save and return updated DTO
            Worklog updatedWorklog = worklogRepository.save(worklog);
            return new ResponseEntity<>(new WorklogDTO(updatedWorklog), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete worklog
     */
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
