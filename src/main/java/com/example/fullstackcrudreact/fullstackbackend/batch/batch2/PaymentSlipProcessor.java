package com.example.fullstackcrudreact.fullstackbackend.batch.batch2;

import java.io.IOException;
import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.example.fullstackcrudreact.fullstackbackend.model.DocumentTemplate;
import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.DocumentTemplateRepository;
import com.example.fullstackcrudreact.fullstackbackend.service.PaymentSlipService;

@Component
public class PaymentSlipProcessor implements ItemProcessor<PaymentSlip, PaymentSlip> {

    @Autowired
    private PaymentSlipService paymentSlipService;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Override
    @Nullable
    public PaymentSlip process(@NonNull PaymentSlip item) throws Exception {
        System.out.println("Processing PaymentSlip: " + item.toString());

        // Retrieve the template
        Optional<DocumentTemplate> templateOptional = documentTemplateRepository.findByTemplateName("HUB3 nalog");

        if (templateOptional.isEmpty()) {
            System.err.println("Template 'HUB3 nalog' not found. Skipping PaymentSlip ID: " + item.getId());
            return null; // Skip this record if template is missing
        }

        DocumentTemplate template = templateOptional.get();

        try {
            // Generate PDF and store the result
            System.out.println("process Generate PDF and store the result");

            byte[] pdfData = paymentSlipService.createPaymentSlipPDF(item, template.getImageData());

            // Assuming PaymentSlip has a field to store PDF bytes
             item.setGeneratedPdf(pdfData);

            return item; // This modified PaymentSlip will be saved in the next batch step
        } catch (IOException e) {
            System.err.println("Error generating PDF for PaymentSlip ID: " + item.getId());
            e.printStackTrace();
            return null; // Skip this record if PDF generation fails
        }
    }
}
