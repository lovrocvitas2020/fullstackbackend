package com.example.fullstackcrudreact.fullstackbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    private String name; // Boat name (e.g., "Swift Racing 4X")
 
    private String type; // Type (e.g., "Single", "Double", "Quad", "Eight")
  
    private int seats; // Number of rowers (1, 2, 4, 8)
  
    private String material; // Boat material (e.g., "Carbon Fiber", "Wood")
  
    private int year; // Year of manufacture
 
    private String boatCondition; // Condition (e.g., "Excellent", "Good", "Needs Repair")
  
    private boolean available; // Is the boat available for use?


    @Override
    public String toString() {
        return "Boat [id=" + id + ", name=" + name + ", type=" + type + ", seats=" + seats + ", material=" + material
                + ", year=" + year + ", boatCondition=" + boatCondition + ", available=" + available + "]";
    }

    // default constructor
    public Boat() {};

    public Boat(Long id, String name, String type, int seats, String material, int year, String boatCondition,
            boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.seats = seats;
        this.material = material;
        this.year = year;
        this.boatCondition = boatCondition;
        this.available = available;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getBoatCondition() {
        return boatCondition;
    }

    public void setBoatCondition(String boatCondition) {
        this.boatCondition = boatCondition;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    

}
