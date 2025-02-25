package com.example.fullstackcrudreact.fullstackbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fullstackcrudreact.fullstackbackend.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

        @Query("SELECT p.projectName FROM Project p INNER JOIN p.tasks t WHERE t.project.id = :projectId")
        String findProjectNameByTaskProjectId(@Param("projectId") Long projectId);

        @Query("SELECT t FROM Task t JOIN FETCH t.project")
        List<Task> findAllWithProject();


}
