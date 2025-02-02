package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstackcrudreact.fullstackbackend.model.Worklog;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Long> {

}
