package com.example.fullstackcrudreact.fullstackbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.fullstackcrudreact.fullstackbackend.model.TrainingRowingLogger;
import com.example.fullstackcrudreact.fullstackbackend.repository.TrainingRowingLoggerRepository;

@Service
public class TrainingRowingLoggerService {

    @Autowired
    private TrainingRowingLoggerRepository trainingRowingLoggerRepository;

    // Get all training logs
    public List<TrainingRowingLogger> getAllLogs() {
        return trainingRowingLoggerRepository.findAll();
    }

    // Get logs by ID
    public Optional<TrainingRowingLogger> getLogById(Long id) {
        return trainingRowingLoggerRepository.findById(id);
    }

    // Get logs by user ID
    public List<TrainingRowingLogger> getLogsByUserId(Long userId) {
        return trainingRowingLoggerRepository.findByUserId(userId);
    }

    // Create new training log
    public TrainingRowingLogger createLog(TrainingRowingLogger log) {
        return trainingRowingLoggerRepository.save(log);
    }

    // Update existing training log
    public Optional<TrainingRowingLogger> updateLog(Long id, TrainingRowingLogger updatedLog) {
        return trainingRowingLoggerRepository.findById(id).map(log -> {
            log.setSessionName(updatedLog.getSessionName());
            log.setDurationMinutes(updatedLog.getDurationMinutes());
            log.setDistanceMeters(updatedLog.getDistanceMeters());
            log.setNotes(updatedLog.getNotes());
            log.setUser(updatedLog.getUser());
            return trainingRowingLoggerRepository.save(log);
        });
    }

    // Delete training log
    public boolean deleteLog(Long id) {
        if (trainingRowingLoggerRepository.existsById(id)) {
            trainingRowingLoggerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Page<TrainingRowingLogger> getAllLogsPaged(Pageable paging) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllLogsPaged'");
    }
}