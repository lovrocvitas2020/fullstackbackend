package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstackcrudreact.fullstackbackend.model.Boat;
import com.example.fullstackcrudreact.fullstackbackend.service.BoatService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Adjust for frontend
public class BoatController {

   
    @Autowired
    private BoatService boatService;

    @GetMapping("/viewboats")
    public List<Boat> getAllBoats() {
        return boatService.getAllBoats();
    }

    @GetMapping("/viewboat/{id}")
    public Optional<Boat> getBoatById(@PathVariable Long id) {
        return boatService.getBoatById(id);
    }

    @PostMapping("/addboat")
    @Transactional
    public Boat addBoat(@RequestBody Boat boat) {
    
        return boatService.addBoat(boat);
    }

    @PutMapping("/updateboat/{id}")
    @Transactional
    public Boat updateBoat(@PathVariable Long id, @RequestBody Boat boat) {
        return boatService.updateBoat(id, boat);
    }

    @DeleteMapping("/deleteboat/{id}")
    public void deleteBoat(@PathVariable Long id) {
        boatService.deleteBoat(id);
    }
}