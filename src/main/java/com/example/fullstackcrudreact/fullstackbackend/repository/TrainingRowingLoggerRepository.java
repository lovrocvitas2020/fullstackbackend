package com.example.fullstackcrudreact.fullstackbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.TrainingRowingLogger;

public interface TrainingRowingLoggerRepository extends JpaRepository<TrainingRowingLogger, Long> {

    // Find all logs by user ID
    List<TrainingRowingLogger> findByUserId(Long userId);

    // Optional: filter by session name
    List<TrainingRowingLogger> findBySessionNameContainingIgnoreCase(String sessionName);
}