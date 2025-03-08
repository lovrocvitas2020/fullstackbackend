package com.example.fullstackcrudreact.fullstackbackend.batch.batch2;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.PaymentSlipRepository;

@Component
public class PaymentSlipTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSlipTasklet.class);

    @Autowired
    private PaymentSlipRepository paymentSlipRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        logger.info("Fetching all payment slips...");
        List<PaymentSlip> paymentSlips = paymentSlipRepository.findAll();

        if (paymentSlips.isEmpty()) {
            logger.info("No payment slips found.");
            return RepeatStatus.FINISHED;
        }

        File outputDir = new File("C:/payment_slips/");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        for (PaymentSlip slip : paymentSlips) {
            try {
                generatePdf(slip, outputDir);
            } catch (IOException e) {
                logger.error("Error generating PDF for payment slip ID: " + slip.getId(), e);
            }
        }

        logger.info("PDF Generation for all payment slips completed.");
        return RepeatStatus.FINISHED;
    }

    private void generatePdf(PaymentSlip slip, File outputDir) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        File fontFile = new File("C:/Windows/Fonts/Arial.ttf"); // Ensure the font file exists
        PDFont font = PDType0Font.load(document, fontFile);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(font, 12);

        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Payment Slip");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Payer: " + slip.getPayerName());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Amount: " + slip.getAmount() + " " + slip.getCurrencyCode());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Recipient: " + slip.getRecipientName());
        contentStream.endText();

        contentStream.close();
        File outputFile = new File(outputDir, "PaymentSlip_" + slip.getId() + ".pdf");
        document.save(outputFile);
        document.close();

        logger.info("Generated PDF for Payment Slip ID: {} at {}", slip.getId(), outputFile.getAbsolutePath());
    }

}
