package com.example.fullstackcrudreact.fullstackbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Equipment name (e.g., "Concept2 Rower")

    private String category; // Category (e.g., "Rower", "Erg", "Life Jacket", "Tool")

    private String brand; // Brand or manufacturer (e.g., "Concept2", "Nike")

    private int year; // Year of manufacture

    private String serialNumber; // Serial Number


    private boolean available; // Is the equipment available for use?

    @Column(name = "conditionofequipment")
    private String conditionOfEquipment; // Condition (e.g., "New", "Good", "Worn", "Needs Repair")

    // Default constructor
    public Equipment() {}

    public Equipment(Long id, String name, String category, String brand, int year, String serialNumber, boolean available, String conditionOfEquipment) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.year = year;
        this.serialNumber = serialNumber;
        this.available = available;
        this.conditionOfEquipment = conditionOfEquipment;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getConditionOfEquipment() {
        return conditionOfEquipment;
    }

      public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setConditionOfEquipment(String conditionOfEquipment) {
        System.out.println("Setting conditionOfEquipment: " + conditionOfEquipment);
        this.conditionOfEquipment = conditionOfEquipment;
    }

    @Override
    public String toString() {
        return "Equipment [id=" + id + ", name=" + name + ", category=" + category + ", brand=" + brand
               + ", serialNumber=" + serialNumber + ", year=" + year + ", available=" + available + ", conditionOfEquipment=" + conditionOfEquipment + "]";
    }

  
}
