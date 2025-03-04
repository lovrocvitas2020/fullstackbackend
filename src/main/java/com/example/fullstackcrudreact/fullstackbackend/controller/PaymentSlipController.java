package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.DocumentTemplate;
import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.DocumentTemplateRepository;
import com.example.fullstackcrudreact.fullstackbackend.repository.PaymentSlipRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins= "http://localhost:3000")
public class PaymentSlipController {

    // Define the maximum size for a LONGBLOB column  
    final long LONGBLOB_MAX_SIZE = 4294967295L;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentSlipController.class);

    @Autowired
    private PaymentSlipRepository paymentSlipRepository;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @GetMapping("/viewpaymentslips")
    public List<PaymentSlip> getAllPaymentSlips() {

        logger.info("Fetching all payment slips");
        return paymentSlipRepository.findAll();
    }

    /**
     *  Gets details for single payment slip
     * 
     * @param id
     * @return
     */
    @GetMapping("/viewpaymentslip/{id}")
    public ResponseEntity<PaymentSlip> getPaymentSlipById(@PathVariable Long id) {
        Optional<PaymentSlip> paymentSlip = paymentSlipRepository.findById(id);

        System.out.println("getPaymentSlipById paymentSlip.toString:"+paymentSlip.toString());


        if (paymentSlip.isPresent()) {
            return ResponseEntity.ok(paymentSlip.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *  Creates new payment slip
     * 
     * 
     * @param paymentSlip
     * @return
     */
    @PostMapping("/addpaymentslips")
    @Transactional
    public ResponseEntity<?> createPaymentSlip(@RequestBody PaymentSlip paymentSlip) {
    try {
        
        System.out.println("createPaymentSlip START");

        // Prepare the data for the barcode
       String barcodeData = prepareBarcodeData(paymentSlip);

       System.out.println("createPaymentSlip barcodeData.toString(): "+barcodeData.toString());
       System.out.println("createPaymentSlip barcodeData.length(): "+barcodeData.length());

       // Validate barcode data size
       if (barcodeData.length() > 1500) {
        throw new IllegalArgumentException("Barcode data exceeds maximum allowed size");
    }

        // Configure the PDF417 barcode generator
        PDF417Writer pdf417Writer = new PDF417Writer();
        Map<EncodeHintType, Object> hints = new HashMap<>();


        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, 4); // Error correction level 4
        hints.put(EncodeHintType.PDF417_COMPACT, true); // Do not use Compact PDF417
        hints.put(EncodeHintType.PDF417_AUTO_ECI, false); // Do not use automatic ECI
        hints.put(EncodeHintType.PDF417_DIMENSIONS, new com.google.zxing.pdf417.encoder.Dimensions(20, 3, 20, 3)); // 9 columns, 3:1 aspect ratio

        // Calculate dimensions based on HUB-3 standard
        int width = 900; // Adjust based on printer resolution (58 mm = ~228 pixels at 100 DPI)
        int height = 300; // Adjust based on printer resolution (26 mm = ~102 pixels at 100 DPI)

        // Generate the PDF417 barcode

        System.out.println("Test sa barcodeText formatiranim tekstom");
        BitMatrix bitMatrix = pdf417Writer.encode(barcodeData, BarcodeFormat.PDF_417, width, height, hints); // tu puca com.google.zxing.WriterException: Unable to fit message in columns

        // Get the number of columns and rows
        int numColumns = bitMatrix.getWidth();
        int numRows = bitMatrix.getHeight();

        // Calculate the total width and height of the image
        int totalWidth = numColumns * width;
        int totalHeight = numRows * height;

        System.out.println("Generated barcode image size: " + totalWidth + "x" + totalHeight + " pixels");

        // Convert BitMatrix to BufferedImage
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convert BufferedImage to Byte Array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(barcodeImage, "png", baos);
        byte[] barcodeBytes = baos.toByteArray();

        logger.info("Generated barcode size: {} bytes", barcodeBytes.length);

            // Ensure the size is within the database column limit
            if (barcodeBytes.length > LONGBLOB_MAX_SIZE) {
                throw new IllegalStateException("Generated barcode is too large for the database column");
            }

            System.out.println("barcodeBytes.length: "+barcodeBytes.length);

        // Set the barcode in the PaymentSlip entity
        paymentSlip.setGeneratedQRcode(barcodeBytes);

        


        // Save the PaymentSlip entity
        PaymentSlip savedPaymentSlip = paymentSlipRepository.save(paymentSlip);
        return ResponseEntity.ok(savedPaymentSlip);
    } catch (Exception e) {
        logger.error("Error generating PDF_417 barcode for PaymentSlip: {}", paymentSlip, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating barcode: " + e.getMessage());
    }
}

    /**
     *  Updates payment slip
     * 
     * @param id
     * @param paymentSlipDetails
     * @return
     */
    @PutMapping("/editpaymentslip/{id}")
    public ResponseEntity<PaymentSlip> updatePaymentSlip(@PathVariable Long id, @RequestBody PaymentSlip paymentSlipDetails) {
        Optional<PaymentSlip> paymentSlip = paymentSlipRepository.findById(id);
        if (paymentSlip.isPresent()) {
            PaymentSlip updatedPaymentSlip = paymentSlip.get();
    
            // Update fields
            updatedPaymentSlip.setAmount(paymentSlipDetails.getAmount());
            updatedPaymentSlip.setCallModelNumber(paymentSlipDetails.getCallModelNumber());
            updatedPaymentSlip.setCurrencyCode(paymentSlipDetails.getCurrencyCode());
            updatedPaymentSlip.setDescription(paymentSlipDetails.getDescription());
            updatedPaymentSlip.setModelNumber(paymentSlipDetails.getModelNumber());
            updatedPaymentSlip.setPayerAddress(paymentSlipDetails.getPayerAddress());
            updatedPaymentSlip.setPayerCity(paymentSlipDetails.getPayerCity());
            updatedPaymentSlip.setPayerName(paymentSlipDetails.getPayerName());
            updatedPaymentSlip.setPurposeCode(paymentSlipDetails.getPurposeCode());
            updatedPaymentSlip.setRecipientAccount(paymentSlipDetails.getRecipientAccount());
            updatedPaymentSlip.setRecipientAddress(paymentSlipDetails.getRecipientAddress());
            updatedPaymentSlip.setRecipientCity(paymentSlipDetails.getRecipientCity());
            updatedPaymentSlip.setRecipientName(paymentSlipDetails.getRecipientName());
    
            // Generate and update the QR code
            try {
                PDF417Writer pdf417Writer = new PDF417Writer(); // Use PDF417Writer
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    
                // Generate the barcode (adjust width & height for clarity)
                BitMatrix bitMatrix = pdf417Writer.encode(updatedPaymentSlip.toString(), BarcodeFormat.PDF_417, 300, 100, hints);
    
                // Convert BitMatrix to BufferedImage
                BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
    
                // Convert BufferedImage to Byte Array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(barcodeImage, "png", baos);
                byte[] barcodeBytes = baos.toByteArray();
    
                // Set the QR code in the entity
                updatedPaymentSlip.setGeneratedQRcode(barcodeBytes);
    
            } catch (Exception e) {
                logger.error("Error generating PDF_417 barcode for PaymentSlip: {}", updatedPaymentSlip, e);
            }
    
            return ResponseEntity.ok(paymentSlipRepository.save(updatedPaymentSlip));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *  Deletes payment slip
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/deletepaymentslip/{id}")
    public ResponseEntity<Void> deletePaymentSlip(@PathVariable Long id) {
        Optional<PaymentSlip> paymentSlip = paymentSlipRepository.findById(id);
        if (paymentSlip.isPresent()) {
            paymentSlipRepository.delete(paymentSlip.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


        
    /**
     *  Method for preparing barcode data 
     * 
     * @param paymentSlip
     * @return
     */
    private String prepareBarcodeData(PaymentSlip paymentSlip) {
        StringBuilder barcodeData = new StringBuilder();

        // Header (8 characters)
        barcodeData.append("HRVHUB30").append("\n");

        // Currency (3 characters)
        barcodeData.append(formatField(paymentSlip.getCurrencyCode(), 3)).append("\n");

        // Amount (15 characters, right-aligned, padded with leading zeros)
        double amountInEuros;
        try {
            amountInEuros = Double.parseDouble(paymentSlip.getAmount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + paymentSlip.getAmount(), e);
        }
        long amountInCents = (long) (amountInEuros * 100);
        barcodeData.append(String.format("%015d", amountInCents)).append("\n");

        // Payer Information
        barcodeData.append(formatField(paymentSlip.getPayerName(), 30)).append("\n");
        barcodeData.append(formatField(paymentSlip.getPayerAddress(), 27)).append("\n");
        barcodeData.append(formatField(paymentSlip.getPayerCity(), 27)).append("\n");

        // Recipient Information
        barcodeData.append(formatField(paymentSlip.getRecipientName(), 25)).append("\n");
        barcodeData.append(formatField(paymentSlip.getRecipientAddress(), 25)).append("\n");
        barcodeData.append(formatField(paymentSlip.getRecipientCity(), 27)).append("\n");

        // Validate IBAN
        String recipientIban = paymentSlip.getRecipientAccount();
        if (!isValidIBAN(recipientIban)) {
            throw new IllegalArgumentException("Invalid IBAN: " + recipientIban);
        }
        barcodeData.append(recipientIban).append("\n");

        // Model Control Number (4 characters)
        barcodeData.append("HR").append(formatField(paymentSlip.getModelNumber(), 2)).append("\n");

        // Call to Number (22 characters)
        barcodeData.append(formatField(paymentSlip.getCallModelNumber(), 22)).append("\n");

        // Purpose Code (4 characters)
        barcodeData.append(formatField(paymentSlip.getPurposeCode(), 4)).append("\n");

        // Payment Description (35 characters)
        barcodeData.append(formatField(paymentSlip.getDescription(), 35));

        return barcodeData.toString();
    }

    // Utility method to handle field length & null values
    private String formatField(String value, int maxLength) {
        if (value == null) {
            value = "";
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    /**
     * Method for IBAN validation using Modulo 97 check (ISO 13616 standard)
     * 
     * @param iban
     * @return
     */
    private boolean isValidIBAN(String iban) {
        if (iban == null || iban.length() < 15 || iban.length() > 34) {
            return false; // IBAN length should be between 15 and 34 characters
        }

        // Move first four characters to the end
        String rearrangedIban = iban.substring(4) + iban.substring(0, 4);

        // Convert letters to numbers (A=10, B=11, ..., Z=35)
        StringBuilder numericIban = new StringBuilder();
        for (char ch : rearrangedIban.toCharArray()) {
            if (Character.isDigit(ch)) {
                numericIban.append(ch);
            } else {
                numericIban.append(ch - 'A' + 10);
            }
        }

        // Convert to BigInteger and check modulo 97
        BigInteger ibanNumber = new BigInteger(numericIban.toString());
        return ibanNumber.mod(BigInteger.valueOf(97)).intValue() == 1;
    }

    /**
     * Method for generating HUB3 payment slip 
     * 
     * @param id
     * @return
     */
    @GetMapping("/generatepdf/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {

        System.out.println("Method generatePdf for id: "+id);


        Optional<PaymentSlip> paymentSlipOptional = paymentSlipRepository.findById(id);

        if (paymentSlipOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

    

        Optional<DocumentTemplate> templateOptional = documentTemplateRepository.findByTemplateName("HUB3 nalog");
        if (templateOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Template 'HUB3 nalog' not found").getBytes());
        } else {
            System.out.println("Template found!");
        }

        PaymentSlip slip = paymentSlipOptional.get();
        DocumentTemplate template = templateOptional.get();

         // Generate the PDF using the template
            byte[] pdfBytes;
        try {
            pdfBytes = createPaymentSlipPDF(slip, template.getImageData()); // Pass template data
        } catch (IOException e) {
            logger.error("Error generating PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "payment_slip_" + id + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Method for creating payment slips
     */
    private byte[] createPaymentSlipPDF(PaymentSlip slip, byte[] templateData) throws IOException {
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
        addText(contentStream, "" + slip.getPayerName(), 25, 520);
        addText(contentStream, "" + slip.getPayerAddress(), 25, 505);
        addText(contentStream, "" + slip.getPayerCity(), 25, 490);
        addText(contentStream, "" + slip.getRecipientName(), 25, 440);
        addText(contentStream, "" + slip.getRecipientAddress(), 25, 425);
        addText(contentStream, "" + slip.getRecipientCity(), 25, 410);
        addText(contentStream, "" + slip.getAmount() , 280, 530);
        addText(contentStream, "" + slip.getCurrencyCode(), 215, 530);
        addText(contentStream, "" + slip.getRecipientAccount(), 215, 475);
        addText(contentStream, "" + slip.getPurposeCode(), 148, 424);
        addText(contentStream, "" + slip.getModelNumber(), 148, 452);
        addText(contentStream, "" + slip.getCallModelNumber(), 205, 452);
        addText(contentStream, "Printed by CvitasTech" , 250, 330);
        addText(contentStream, "" + slip.getDescription(), 233, 435);
       

        // again but with smaller font size
        contentStream.setFont(font, 10);
        addText(contentStream, "" + slip.getAmount() , 435, 530);      
        addText(contentStream, "" + slip.getModelNumber(), 435, 490);
        addText(contentStream, "   " + slip.getCallModelNumber(), 445, 490);
        addText(contentStream, "" + slip.getRecipientAccount(), 435, 473);
        addText(contentStream, "" + slip.getDescription(), 435, 430);
    
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
