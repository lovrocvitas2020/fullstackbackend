package com.example.fullstackcrudreact.fullstackbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstackcrudreact.fullstackbackend.model.DocumentTemplate;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

    Optional<DocumentTemplate> findByTemplateName(String templateName);
}
