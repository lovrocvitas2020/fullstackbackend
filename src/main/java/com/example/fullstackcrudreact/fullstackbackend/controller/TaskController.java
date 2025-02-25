package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
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

import com.example.fullstackcrudreact.fullstackbackend.model.Project;
import com.example.fullstackcrudreact.fullstackbackend.model.Task;
import com.example.fullstackcrudreact.fullstackbackend.repository.ProjectRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.TaskRepository;
import com.example.fullstackcrudreact.fullstackbackend.service.TaskService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins= "http://localhost:3000")
public class TaskController {


     private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TaskController.class);

   
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/viewtasks")
    public ResponseEntity<List<Task>> getAllTasks() {

        System.out.println("viewtasks start");
        
      List<Task> tasks = taskRepository.findAllWithProject();
      // Ensure the project name is set for each task
      tasks.forEach(task -> task.setProjectName(task.getProject().getProjectName()));
      return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/addtask")
    @Transactional
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> payload) {
        logger.info("createTask -> addtask: " + payload);
    
        // Extract project_id from payload
        Long projectId = ((Number) payload.get("project_id")).longValue();
        if (projectId == null) {
            throw new RuntimeException("Project ID is required");
        }
    
        // Fetch the Project entity
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
    
        // Create Task manually
        Task task = new Task();
        task.setTaskName((String) payload.get("taskName"));
        task.setDescription((String) payload.get("description"));
        task.setDueDate(Timestamp.valueOf(payload.get("dueDate") + " 00:00:00"));
        task.setStatus(Task.Status.valueOf((String) payload.get("status")));
        task.setProject(project); // Assign the project

        System.out.println("get project: "+task.getProject());
    
        // Save Task
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
