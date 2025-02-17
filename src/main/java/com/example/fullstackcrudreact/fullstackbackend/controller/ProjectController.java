package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.Project;
import com.example.fullstackcrudreact.fullstackbackend.repository.ProjectRepository;

import jakarta.transaction.Transactional;


@RestController
@CrossOrigin(origins= "http://localhost:3000")
public class ProjectController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectController.class);


    @Autowired
    private ProjectRepository projectRepository;


    @GetMapping("/viewprojects")
    public List<Project> getAllProjects() {
        return projectRepository.findAll() ;
    }
    
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        return ResponseEntity.ok(project);
    }

    @PostMapping("/addprojects")
    @Transactional
    public Project createProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }

    @PutMapping("/editproject/{id}")
    @Transactional
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        project.setProjectName(projectDetails.getProjectName());
        project.setDescription(projectDetails.getDescription());

        Project updatedProject = projectRepository.save(project);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        projectRepository.delete(project);
        return ResponseEntity.noContent().build();
    }

}
