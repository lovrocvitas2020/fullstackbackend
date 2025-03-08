package com.example.fullstackcrudreact.fullstackbackend.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;

@Service
public class PaymentSlipService {

    /**
     *  Method for writing payment slip
     * 
     * @param slip
     * @param templateData
     * @return
     * @throws IOException
     */
    public byte[] createPaymentSlipPDF(PaymentSlip slip, byte[] templateData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
        // Load the PDF template
        PDDocument document = PDDocument.load(templateData);
        PDPage page = document.getPage(0); // Assuming template has at least one page

        // Load a Unicode-supported font
        File fontFile = new File("C:/Windows/Fonts/Arial.ttf"); // Adjust path as needed
        PDFont font = PDType0Font.load(document, fontFile);
    
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
    
        // Retrieve image from PaymentSlip entity (QR code)
        byte[] imageData = slip.getGeneratedQRcode();
        if (imageData != null && imageData.length > 0) {
            PDImageXObject barcodeImage = PDImageXObject.createFromByteArray(document, imageData, "barcode");
            contentStream.drawImage(barcodeImage, 30, 320, 160, 80); // Adjust position & size
        }
    
        // Set font for text overlay
        contentStream.setFont(font, 12);
    
        // Overlay payment slip data
        addText(contentStream, slip.getPayerName(), 25, 520);
        addText(contentStream, slip.getPayerAddress(), 25, 505);
        addText(contentStream, slip.getPayerCity(), 25, 490);
        addText(contentStream, slip.getRecipientName(), 25, 440);
        addText(contentStream, slip.getRecipientAddress(), 25, 425);
        addText(contentStream, slip.getRecipientCity(), 25, 410);
        addText(contentStream, slip.getAmount(), 280, 530);
        addText(contentStream, slip.getCurrencyCode(), 215, 530);
        addText(contentStream, slip.getRecipientAccount(), 215, 475);
        addText(contentStream, slip.getPurposeCode(), 148, 424);
        addText(contentStream, slip.getModelNumber(), 148, 452);
        addText(contentStream, slip.getCallModelNumber(), 205, 452);
        addText(contentStream, "Printed by CvitasTech", 250, 330);
        addText(contentStream, slip.getDescription(), 233, 435);

        contentStream.close();
        document.save(outputStream);
        document.close();
    
        return outputStream.toByteArray();
    }

    private void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}
