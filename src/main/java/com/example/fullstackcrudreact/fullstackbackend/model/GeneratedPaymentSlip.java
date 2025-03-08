package com.example.fullstackcrudreact.fullstackbackend.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;

@Entity
public class GeneratedPaymentSlip {

    @Id
    @GeneratedValue
    private Long id;

    @Length(max = 3)
    private String currencyCode;

    private String amount;

    @Length(max = 30)
    private String payerName;

    @Length(max = 27)
    private String payerAddress;

    @Length(max = 27)
    private String payerCity;

    @Length(max = 25)
    private String recipientName;

    @Length(max = 27)
    private String recipientAddress;

    @Length(max = 27)
    private String recipientCity;

    private String recipientAccount;

    @Length(max = 4)
    private String modelNumber;

    @Length(max = 35)
    private String callModelNumber;

    @Length(max = 4)
    private String purposeCode;

    @Length(max = 35)
    private String description;

    @NonNull
    private Timestamp createdOn; 

    private Timestamp generatedOn;

    private boolean isMarkedForSending;

    @Lob
    @Column(columnDefinition = "LONGBLOB") // Allows large binary data storage
    @JsonIgnore
    private byte[] generatedQRcode;

    // Getter for Base64-encoded QR Code to send to the frontend
    @JsonProperty("generatedQRcode")
    public String getGeneratedQRcodeBase64() {
        return (generatedQRcode != null) ? Base64.getEncoder().encodeToString(generatedQRcode) : null;
    }

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] generatedPdf; // New field to store PDF

    // Getter for Base64-encoded PDF to send to the frontend
    @JsonProperty("generatedPdf")
    public String getGeneratedPdfBase64() {
        return (generatedPdf != null) ? Base64.getEncoder().encodeToString(generatedPdf) : null;
    }

    @PrePersist
    protected void onCreate() {
        createdOn = Timestamp.from(Instant.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerAddress() {
        return payerAddress;
    }

    public void setPayerAddress(String payerAddress) {
        this.payerAddress = payerAddress;
    }

    public String getPayerCity() {
        return payerCity;
    }

    public void setPayerCity(String payerCity) {
        this.payerCity = payerCity;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getRecipientCity() {
        return recipientCity;
    }

    public void setRecipientCity(String recipientCity) {
        this.recipientCity = recipientCity;
    }

    public String getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(String recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getCallModelNumber() {
        return callModelNumber;
    }

    public void setCallModelNumber(String callModelNumber) {
        this.callModelNumber = callModelNumber;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isMarkedForSending() {
        return isMarkedForSending;
    }

    public void setMarkedForSending(boolean isMarkedForSending) {
        this.isMarkedForSending = isMarkedForSending;
    }

    public byte[] getGeneratedQRcode() {
        return generatedQRcode;
    }

    public void setGeneratedQRcode(byte[] generatedQRcode) {
        this.generatedQRcode = generatedQRcode;
    }

    public byte[] getGeneratedPdf() {
        return generatedPdf;
    }

    public void setGeneratedPdf(byte[] generatedPdf) {
        this.generatedPdf = generatedPdf;
    }

    @Override
    public String toString() {
        return "GeneratedPaymentSlip [id=" + id + ", currencyCode=" + currencyCode + ", amount=" + amount
                + ", payerName=" + payerName + ", payerAddress=" + payerAddress + ", payerCity=" + payerCity
                + ", recipientName=" + recipientName + ", recipientAddress=" + recipientAddress + ", recipientCity="
                + recipientCity + ", recipientAccount=" + recipientAccount + ", modelNumber=" + modelNumber
                + ", callModelNumber=" + callModelNumber + ", purposeCode=" + purposeCode + ", description="
                + description + ", createdOn=" + createdOn + ", isMarkedForSending=" + isMarkedForSending
                + ", generatedQRcode=" + Arrays.toString(generatedQRcode) + ", generatedPdf="
                + Arrays.toString(generatedPdf) + "]";
    }

    public Timestamp getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(Timestamp generatedOn) {
        this.generatedOn = generatedOn;
    }

    

}
