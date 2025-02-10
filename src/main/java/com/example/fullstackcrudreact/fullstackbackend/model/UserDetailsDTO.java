package com.example.fullstackcrudreact.fullstackbackend.model;

import java.time.LocalDate;


public class UserDetailsDTO {
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private String sex; // Use String instead of the enum
    private String countryOfBirth;
    private String city;
    private String zipCode;
    private Long userId;

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserDetails toEntity() {
        UserDetails userDetails = new UserDetails();
        userDetails.setPhoneNumber(this.phoneNumber);
        userDetails.setAddress(this.address);
        userDetails.setDateOfBirth(this.dateOfBirth);
        userDetails.setSex(UserDetails.Sex.valueOf(this.sex.toUpperCase())); // Convert to enum
        userDetails.setCountryOfBirth(this.countryOfBirth);
        userDetails.setCity(this.city);
        userDetails.setZipCode(this.zipCode);
        return userDetails;
    }
}