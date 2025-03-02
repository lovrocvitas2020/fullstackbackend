package com.example.fullstackcrudreact.fullstackbackend.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.fullstackcrudreact.fullstackbackend.model.DocumentTemplate;
import com.example.fullstackcrudreact.fullstackbackend.repository.DocumentTemplateRepository;

@Service
public class DocumentTemplateService {


    private final DocumentTemplateRepository documentTemplateRepository;

      @Autowired
    public DocumentTemplateService(DocumentTemplateRepository documentTemplateRepository) {
        this.documentTemplateRepository = documentTemplateRepository;
    }

     // Create or Update document template
    public DocumentTemplate saveDocumentTemplate(String templateName, MultipartFile file) throws IOException {
        DocumentTemplate documentTemplate = new DocumentTemplate();
        documentTemplate.setTemplateName(templateName);
        documentTemplate.setImageData(file.getBytes());

        return documentTemplateRepository.save(documentTemplate);
    }

    // Get a document template by ID
    public Optional<DocumentTemplate> getDocumentTemplate(Long id) {
        return documentTemplateRepository.findById(id);
    }

    // Get all document templates
    public Iterable<DocumentTemplate> getAllDocumentTemplates() {
        return documentTemplateRepository.findAll();
    }

    // Delete document template by ID
    public void deleteDocumentTemplate(Long id) {
        documentTemplateRepository.deleteById(id);
    }

}
