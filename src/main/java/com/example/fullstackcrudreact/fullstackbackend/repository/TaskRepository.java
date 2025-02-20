package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstackcrudreact.fullstackbackend.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
