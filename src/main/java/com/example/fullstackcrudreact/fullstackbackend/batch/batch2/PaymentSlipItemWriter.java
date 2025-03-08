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
            generatePdfForPaymentSlip(slip);
            insertGeneratedPaymentSlip(slip);
        }
    }


    /* 
    @Override
    public void write(List<? extends PaymentSlip> paymentSlips) throws Exception {
        for (PaymentSlip slip : paymentSlips) {
            generatePdfForPaymentSlip(slip);
        }
    }
    */

    /**
     * Method for generating payment slips 
     * 
     * @param slip
     */
    private void generatePdfForPaymentSlip(PaymentSlip slip) {
        try {
            // Fetch template from database
            Optional<DocumentTemplate> templateOptional = documentTemplateRepository.findByTemplateName("HUB3 nalog");

            if (templateOptional.isEmpty()) {
                logger.error("Template 'HUB3 nalog' not found for Payment Slip ID: {}", slip.getId());
                return;
            }

            DocumentTemplate template = templateOptional.get();
            byte[] pdfBytes = createPaymentSlipPDF(slip, template.getImageData());

            UUID uuid = UUID.randomUUID();
            System.out.println("Generated UUID: " + uuid.toString());
            System.out.println("payer name:"+slip.getPayerName());

            // Define file path (adjust as needed)
            String outputFilePath = "generated_pdfs/Uplatnica_" +slip.getPayerName()+"_"+slip.getId()+"_"+uuid.toString()+".pdf";
            File outputFile = new File(outputFilePath);
            outputFile.getParentFile().mkdirs(); // Ensure the directory exists
       
            // Write PDF to file
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(pdfBytes);
                logger.info("Generated PDF for Payment Slip ID: {}", slip.getId()+uuid.toString());
            }

        } catch (IOException e) {
            logger.error("Error generating PDF for Payment Slip ID: {}", slip.getId(), e);
        }
    }

    /**
     * Inserts generated payment slip data into the database
     */
    private void insertGeneratedPaymentSlip(PaymentSlip slip) {
        GeneratedPaymentSlip generatedSlip = new GeneratedPaymentSlip();
        generatedSlip.setId(slip.getId());
        generatedSlip.setAmount(slip.getAmount());
        generatedSlip.setCurrencyCode(slip.getCurrencyCode());
        generatedSlip.setCallModelNumber(slip.getCallModelNumber());
        generatedSlip.setPayerName(slip.getPayerName());
        generatedSlip.setPayerAddress(slip.getPayerAddress());
        generatedSlip.setPayerCity(slip.getPayerCity());
        generatedSlip.setRecipientName(slip.getRecipientName());
        generatedSlip.setRecipientAddress(slip.getRecipientAddress());
        generatedSlip.setRecipientCity(slip.getRecipientAddress());
        generatedSlip.setDescription(slip.getDescription());
        generatedSlip.setModelNumber(slip.getModelNumber());
        generatedSlip.setPurposeCode(slip.getPurposeCode());
        generatedSlip.setGeneratedOn(Timestamp.from(Instant.now()));
        generatedSlip.setRecipientAccount(slip.getRecipientAccount());

        generatedPaymentSlipRepository.save(generatedSlip);
        logger.info("Inserted payment slip record for ID: {}", slip.getId());
    }
    
    /**
     * Method for creating payment slip from template
     * 
     * @param slip
     * @param templateData
     * @return
     * @throws IOException
     */
    private byte[] createPaymentSlipPDF(PaymentSlip slip, byte[] templateData) throws IOException {

            return paymentSlipService.createPaymentSlipPDF(slip, templateData);
      
    }

  
}
