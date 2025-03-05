package com.example.fullstackcrudreact.fullstackbackend.model;

import java.sql.Timestamp;
import java.time.Instant;
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
public class PaymentSlip {

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

    @Lob
    @Column(columnDefinition = "LONGBLOB") // Allows large binary data storage
    @JsonIgnore
    private byte[] generatedQRcode;

    // Getter for Base64-encoded QR Code to send to the frontend
    @JsonProperty("generatedQRcode")
    public String getGeneratedQRcodeBase64() {
        return (generatedQRcode != null) ? Base64.getEncoder().encodeToString(generatedQRcode) : null;
    }

    @PrePersist
    protected void onCreate() {
        createdOn = Timestamp.from(Instant.now());
    }

    // Default constructor
    public PaymentSlip() {}

    // Full-argument constructor
    public PaymentSlip(String amount, String callModelNumber, String currencyCode, String description, Long id, 
                       String modelNumber, String payerAddress, String payerCity, String payerName, 
                       String purposeCode, String recipientAccount, String recipientAddress, 
                       String recipientCity, String recipientName, byte[] generatedQRcode) {
        this.amount = amount;
        this.callModelNumber = callModelNumber;
        this.currencyCode = currencyCode;
        this.description = description;
        this.id = id;
        this.modelNumber = modelNumber;
        this.payerAddress = payerAddress;
        this.payerCity = payerCity;
        this.payerName = payerName;
        this.purposeCode = purposeCode;
        this.recipientAccount = recipientAccount;
        this.recipientAddress = recipientAddress;
        this.recipientCity = recipientCity;
        this.recipientName = recipientName;
        this.generatedQRcode = generatedQRcode;
    }

    // Getters and Setters
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

    public byte[] getGeneratedQRcode() {
        return generatedQRcode;
    }

    public void setGeneratedQRcode(byte[] generatedQRcode) {
        this.generatedQRcode = generatedQRcode;
    }

    @Override
    public String toString() {
        return "PaymentSlip [id=" + id + ", currencyCode=" + currencyCode + ", amount=" + amount + ", payerName="
                + payerName + ", payerAddress=" + payerAddress + ", payerCity=" + payerCity + ", recipientName="
                + recipientName + ", recipientAddress=" + recipientAddress + ", recipientCity=" + recipientCity
                + ", recipientAccount=" + recipientAccount + ", modelNumber=" + modelNumber + ", callModelNumber="
                + callModelNumber + ", purposeCode=" + purposeCode + ", description=" + description + "]";
    }
}
