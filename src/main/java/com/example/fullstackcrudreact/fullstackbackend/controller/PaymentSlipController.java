package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
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

    @GetMapping("/viewpaymentslips")
    public List<PaymentSlip> getAllPaymentSlips() {

        logger.info("Fetching all payment slips");
        return paymentSlipRepository.findAll();
    }

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


    private String prepareBarcodeData(PaymentSlip paymentSlip) {
        StringBuilder barcodeData = new StringBuilder();
    
        // Header (8 characters)
        barcodeData.append("HRVHUB30").append("\n");
    
        // Currency (3 characters)
        barcodeData.append(paymentSlip.getCurrencyCode()).append("\n");
    
        // Amount (15 characters, right-aligned, padded with leading zeros)
        // Parse the amount from String to double
        double amountInEuros;
        try {
            amountInEuros = Double.parseDouble(paymentSlip.getAmount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + paymentSlip.getAmount(), e);
        }
    
        // Convert amount from euros to eurocents (e.g., 123.35 EUR -> 12335)
        long amountInCents = (long) (amountInEuros * 100);
        String amount = String.format("%015d", amountInCents); // Format as 15-digit integer
        barcodeData.append(amount).append("\n");
    
        // Payer Name (30 characters)
        barcodeData.append(paymentSlip.getPayerName()).append("\n");
    
        // Payer Address (Street and Number, 27 characters)
        barcodeData.append(paymentSlip.getPayerAddress()).append("\n");
    
        // Payer Address (Postal Code and City, 27 characters)
        barcodeData.append(paymentSlip.getPayerCity()).append("\n");
    
        // Recipient Name (25 characters)
        barcodeData.append(paymentSlip.getRecipientName()).append("\n");
    
        // Recipient Address (Street and Number, 25 characters)
        barcodeData.append(paymentSlip.getRecipientAddress()).append("\n");
    
        // Recipient Address (Postal Code and City, 27 characters)
        barcodeData.append(paymentSlip.getRecipientCity()).append("\n");
    
        // Recipient Account (IBAN, 21 characters)
        barcodeData.append(paymentSlip.getRecipientAccount()).append("\n");
    
        // Model Control Number (4 characters)
        barcodeData.append("HR").append(paymentSlip.getModelNumber()).append("\n");
    
        // Call to Number (22 characters)
        barcodeData.append(paymentSlip.getCallModelNumber()).append("\n");
    
        // Purpose Code (4 characters)
        barcodeData.append(paymentSlip.getPurposeCode()).append("\n");
    
        // Payment Description (35 characters)
        barcodeData.append(paymentSlip.getDescription());
    
        return barcodeData.toString();
    }


}
