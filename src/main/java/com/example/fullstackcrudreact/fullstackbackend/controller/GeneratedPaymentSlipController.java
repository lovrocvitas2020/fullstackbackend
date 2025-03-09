package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.GeneratedPaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.GeneratedPaymentSlipRepository;

@RestController
@CrossOrigin(origins= "http://localhost:3000")
public class GeneratedPaymentSlipController {

    private final GeneratedPaymentSlipRepository generatedPaymentSlipRepository;

    public GeneratedPaymentSlipController(GeneratedPaymentSlipRepository generatedPaymentSlipRepository) {
        this.generatedPaymentSlipRepository = generatedPaymentSlipRepository;
    }


    /**
     * Fetch all generated payment slips
     */
    @GetMapping("/viewgeneratedpaymentslips")
    public ResponseEntity<List<GeneratedPaymentSlip>> getAllGeneratedPaymentSlips() {
        List<GeneratedPaymentSlip> slips = generatedPaymentSlipRepository.findAll();
        return ResponseEntity.ok(slips);
    }

    /**
     * Fetch Generated Payment Slip by ID (Returns JSON with Base64 PDF)
     */
    @GetMapping("/getgeneratedpaymentslip/{id}")
    public ResponseEntity<?> getGeneratedPaymentSlip(@PathVariable Long id) {
        Optional<GeneratedPaymentSlip> slipOptional = generatedPaymentSlipRepository.findById(id);
        
        if (slipOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment slip not found");
        }

        return ResponseEntity.ok(slipOptional.get());
    }

    /**
     * Fetch PDF as a downloadable file
     */
    @GetMapping("/generatedpaymentslips/pdf/{id}")
    public ResponseEntity<byte[]> downloadGeneratedPdf(@PathVariable Long id) {
        Optional<GeneratedPaymentSlip> slipOptional = generatedPaymentSlipRepository.findById(id);

        if (slipOptional.isEmpty() || slipOptional.get().getGeneratedPdf() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        byte[] pdfBytes = slipOptional.get().getGeneratedPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "GeneratedPaymentSlip_" + id + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Delete a generated payment slip by ID
     */
    @DeleteMapping("/deletegeneratedpaymentslip/{id}")
    public ResponseEntity<String> deleteGeneratedPaymentSlip(@PathVariable Long id) {
        Optional<GeneratedPaymentSlip> slipOptional = generatedPaymentSlipRepository.findById(id);

        if (slipOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment slip not found");
        }

        generatedPaymentSlipRepository.deleteById(id);
        return ResponseEntity.ok("Payment slip deleted successfully");
    }
}
