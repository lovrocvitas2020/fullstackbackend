package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.exception.ResourceNotFoundException;
import com.example.fullstackcrudreact.fullstackbackend.model.TrainingLogDto;
import com.example.fullstackcrudreact.fullstackbackend.model.TrainingRowingLogger;
import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.TrainingRowingLoggerRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;
import com.example.fullstackcrudreact.fullstackbackend.service.TrainingRowingLoggerService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TrainingRowingLoggerController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TrainingRowingLoggerController.class);

    private final TrainingRowingLoggerService trainingRowingLoggerService;
    private final PagedResourcesAssembler<TrainingRowingLogger> pagedResourcesAssembler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TrainingRowingLoggerRepository trainingRowingLoggerRepository;

    @Autowired
    public TrainingRowingLoggerController(TrainingRowingLoggerService trainingRowingLoggerService,
                                          PagedResourcesAssembler<TrainingRowingLogger> pagedResourcesAssembler) {
        this.trainingRowingLoggerService = trainingRowingLoggerService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping("/viewtrainings")
    public List<TrainingRowingLogger> getAllLogs() {
        return trainingRowingLoggerRepository.findAll();
    }


   
            /** Create New Log */
        @PostMapping("/addtraining")
        @Transactional
        public ResponseEntity<TrainingRowingLogger> createLog(@RequestBody TrainingLogDto dto) {
            logger.debug("Received DTO: {}", dto);

            User user = userRepository.findById(dto.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.userId));

            TrainingRowingLogger log = new TrainingRowingLogger();
            log.setSessionName(dto.sessionName);
            log.setDurationMinutes(dto.durationMinutes);
            log.setDistanceMeters(dto.distanceMeters);
            log.setNotes(dto.notes);
            log.setUser(user);
            log.setCreatedOn(Timestamp.from(Instant.now()));

            return ResponseEntity.ok(trainingRowingLoggerService.createLog(log));
        }


    /** Update Log */
    @PutMapping("/edittraining/{id}")
    @Transactional
    public ResponseEntity<?> updateLog(@PathVariable Long id, @RequestBody TrainingRowingLogger updatedLog) {
        return trainingRowingLoggerService.updateLog(id, updatedLog)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Training log not found with ID: " + id));
    }

    /** Delete Log */
    @DeleteMapping("/deletetraining/{id}")
    @Transactional
    public ResponseEntity<?> deleteLog(@PathVariable Long id) {
        boolean deleted = trainingRowingLoggerService.deleteLog(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Training log not found with ID: " + id);
        }
        return ResponseEntity.ok("Training log with ID " + id + " has been deleted successfully.");
    }
}