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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.financial.Hub3QrGenerator;
import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.PaymentSlipRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins= "http://localhost:3000")
public class PaymentSlipController {

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
    public PaymentSlip createPaymentSlip(@RequestBody PaymentSlip paymentSlip)  {

        logger.info("createPaymentSlip ADD NEW payment slip");
        System.out.println("createPaymentSlip ADD NEW payment slip: "+paymentSlip.toString());

        // call methood for generating barcode and return back barcode
        Hub3QrGenerator hub3QrGenerator = new Hub3QrGenerator();

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(paymentSlip.toString(), BarcodeFormat.QR_CODE, 100, 100, hints);

            if (bitMatrix == null) {
                throw new RuntimeException("Failed to generate BitMatrix for QR Code");
            }
            
            System.out.println("bitMatrix is generated "+bitMatrix);

                    // Convert BitMatrix to BufferedImage
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos); // "png" format
            byte[] qrCodeBytes = baos.toByteArray();
            
            if(bitMatrix != null){               
                paymentSlip.setGeneratedQRcode(qrCodeBytes);
            }

           
        } catch (Exception e) {
            logger.error("Error generating QR code for PaymentSlip: {}", paymentSlip, e);
            e.printStackTrace();
        }
    


        return paymentSlipRepository.save(paymentSlip);
    }

    @PutMapping("/paymentSlips/{id}")
    public ResponseEntity<PaymentSlip> updatePaymentSlip(@PathVariable Long id, @RequestBody PaymentSlip paymentSlipDetails) {
        Optional<PaymentSlip> paymentSlip = paymentSlipRepository.findById(id);
        if (paymentSlip.isPresent()) {
            PaymentSlip updatedPaymentSlip = paymentSlip.get();
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
            return ResponseEntity.ok(paymentSlipRepository.save(updatedPaymentSlip));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/paymentSlips/{id}")
    public ResponseEntity<Void> deletePaymentSlip(@PathVariable Long id) {
        Optional<PaymentSlip> paymentSlip = paymentSlipRepository.findById(id);
        if (paymentSlip.isPresent()) {
            paymentSlipRepository.delete(paymentSlip.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
