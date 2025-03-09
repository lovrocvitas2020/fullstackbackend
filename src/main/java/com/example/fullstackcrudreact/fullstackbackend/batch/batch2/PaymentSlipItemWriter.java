package com.example.fullstackcrudreact.fullstackbackend.batch.batch2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.fullstackcrudreact.fullstackbackend.model.DocumentTemplate;
import com.example.fullstackcrudreact.fullstackbackend.model.GeneratedPaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.DocumentTemplateRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.GeneratedPaymentSlipRepository;
import com.example.fullstackcrudreact.fullstackbackend.service.PaymentSlipService;

@Component
public class PaymentSlipItemWriter implements ItemWriter<PaymentSlip> {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSlipItemWriter.class);

    @Autowired
    private PaymentSlipService paymentSlipService;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private GeneratedPaymentSlipRepository generatedPaymentSlipRepository;

    @Override
    public void write(Chunk<? extends PaymentSlip> chunk) throws Exception {
        for (PaymentSlip slip : chunk) {
            byte[] generatedPdf = generatePdfForPaymentSlip(slip);
            slip.setGeneratedPdf(generatedPdf); // Set PDF in PaymentSlip
            insertGeneratedPaymentSlip(slip, generatedPdf);
        }
    }

    /**
     * Generates a PDF for a given payment slip and returns the PDF as a byte array.
     */
    private byte[] generatePdfForPaymentSlip(PaymentSlip slip) {
        try {
            // Fetch template from database
            Optional<DocumentTemplate> templateOptional = documentTemplateRepository.findByTemplateName("HUB3 nalog");

            if (templateOptional.isEmpty()) {
                logger.error("Template 'HUB3 nalog' not found for Payment Slip ID: {}", slip.getId());
                return null;
            }

            DocumentTemplate template = templateOptional.get();
            byte[] pdfBytes = createPaymentSlipPDF(slip, template.getImageData());

            UUID uuid = UUID.randomUUID();
            logger.info("Generated UUID: {}", uuid);
            logger.info("Payer name: {}", slip.getPayerName());

            // Define file path
            String outputFilePath = "generated_pdfs/Uplatnica_" + slip.getPayerName() + "_" + slip.getId() + "_" + uuid + ".pdf";
            File outputFile = new File(outputFilePath);

            // Ensure the directory exists
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    logger.error("Failed to create directories for path: {}", outputFilePath);
                    return null;
                }
            }

            // Write PDF to file
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(pdfBytes);
                logger.info("Generated PDF for Payment Slip ID: {} UUID: {}", slip.getId(), uuid);
            }

            return pdfBytes; // Return generated PDF bytes

        } catch (IOException e) {
            logger.error("Error generating PDF for Payment Slip ID: {}", slip.getId(), e);
            return null;
        }
    }

    /**
     * Inserts generated payment slip data into the database, including the PDF bytes.
     */
    private void insertGeneratedPaymentSlip(PaymentSlip slip, byte[] pdfBytes) {
        if (pdfBytes == null) {
            logger.error("Skipping database insert for Payment Slip ID: {} due to PDF generation failure.", slip.getId());
            return;
        }

        GeneratedPaymentSlip generatedSlip = new GeneratedPaymentSlip();
        generatedSlip.setAmount(slip.getAmount());
        generatedSlip.setCurrencyCode(slip.getCurrencyCode());
        generatedSlip.setCallModelNumber(slip.getCallModelNumber());
        generatedSlip.setPayerName(slip.getPayerName());
        generatedSlip.setPayerAddress(slip.getPayerAddress());
        generatedSlip.setPayerCity(slip.getPayerCity());
        generatedSlip.setRecipientName(slip.getRecipientName());
        generatedSlip.setRecipientAddress(slip.getRecipientAddress());
        generatedSlip.setRecipientCity(slip.getRecipientCity());
        generatedSlip.setDescription(slip.getDescription());
        generatedSlip.setModelNumber(slip.getModelNumber());
        generatedSlip.setPurposeCode(slip.getPurposeCode());
        generatedSlip.setGeneratedOn(Timestamp.from(Instant.now()));
        generatedSlip.setRecipientAccount(slip.getRecipientAccount());
        generatedSlip.setGeneratedPdf(pdfBytes);
       
        generatedPaymentSlipRepository.save(generatedSlip);
        logger.info("Inserted payment slip record for ID: {}", slip.getId());
    }

    /**
     * Method for creating payment slip from template
     */
    private byte[] createPaymentSlipPDF(PaymentSlip slip, byte[] templateData) throws IOException {
        return paymentSlipService.createPaymentSlipPDF(slip, templateData);
    }
}
