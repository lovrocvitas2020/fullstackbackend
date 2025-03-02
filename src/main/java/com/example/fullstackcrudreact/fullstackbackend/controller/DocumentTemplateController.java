package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fullstackcrudreact.fullstackbackend.model.DocumentTemplate;
import com.example.fullstackcrudreact.fullstackbackend.service.DocumentTemplateService;

@RestController
@CrossOrigin(origins= "http://localhost:3000")
public class DocumentTemplateController {

    private final DocumentTemplateService documentTemplateService;

    @Autowired
    public DocumentTemplateController(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    // Create or Update document template
    @PostMapping("/documenttemplateupload")
    public ResponseEntity<DocumentTemplate> uploadDocumentTemplate(
            @RequestParam("templateName") String templateName,
            @RequestParam("file") MultipartFile file) {
        try {
            DocumentTemplate savedTemplate = documentTemplateService.saveDocumentTemplate(templateName, file);
            return ResponseEntity.ok(savedTemplate);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get a document template by ID
    @GetMapping("/documenttemplate/{id}")
    public ResponseEntity<DocumentTemplate> getDocumentTemplate(@PathVariable Long id) {
        Optional<DocumentTemplate> documentTemplate = documentTemplateService.getDocumentTemplate(id);
        return documentTemplate.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all document templates
    @GetMapping("/viewdocumenttemplates")
    public Iterable<DocumentTemplate> getAllDocumentTemplates() {
        return documentTemplateService.getAllDocumentTemplates();
    }

    // Delete document template by ID
    @DeleteMapping("/deletedocumenttemplate/{id}")
    public ResponseEntity<Void> deleteDocumentTemplate(@PathVariable Long id) {
        documentTemplateService.deleteDocumentTemplate(id);
        return ResponseEntity.noContent().build();
    }

    // Fetch the image of a document template by ID
    @GetMapping("/documenttemplate/{id}/image")
    public ResponseEntity<byte[]> getDocumentTemplateImage(@PathVariable Long id) {
        Optional<DocumentTemplate> documentTemplate = documentTemplateService.getDocumentTemplate(id);
        
        if (documentTemplate.isPresent() && documentTemplate.get().getImageData() != null) {
            // Assuming getImageData() returns the byte[] of the image
            byte[] imageData = documentTemplate.get().getImageData();
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png") // or "image/jpeg" based on your image format
                    .body(imageData);
        } else {
            return ResponseEntity.notFound().build(); // Image not found
        }
    }
}
