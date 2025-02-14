package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstackcrudreact.fullstackbackend.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
