package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.util.List;
import java.util.Optional;

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

import com.example.fullstackcrudreact.fullstackbackend.model.Equipment;
import com.example.fullstackcrudreact.fullstackbackend.repository.EquipmentRepository;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = "http://localhost:3000") 
public class EquipmentController {


     @Autowired
    private EquipmentRepository equipmentRepository;

    // Get all equipment
    @GetMapping("/viewequipment")
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    
    // Get equipment by ID
    @GetMapping("/viewequipment/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        Optional<Equipment> equipment = equipmentRepository.findById(id);
        return equipment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new equipment
    @PostMapping("/addequipment")
    @Transactional
    public Equipment createEquipment(@RequestBody Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    // Update existing equipment
    @PutMapping("/editequipment/{id}")
    public ResponseEntity<Equipment> editEquipment(@PathVariable Long id, @RequestBody Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setName(updatedEquipment.getName());
            equipment.setCategory(updatedEquipment.getCategory());
            equipment.setBrand(updatedEquipment.getBrand());
            equipment.setYear(updatedEquipment.getYear());
            equipment.setAvailable(updatedEquipment.isAvailable());
            equipment.setConditionOfEquipment(updatedEquipment.getConditionOfEquipment());
            equipment.setSerialNumber(updatedEquipment.getSerialNumber());
            return ResponseEntity.ok(equipmentRepository.save(equipment));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete equipment
    @DeleteMapping("/deleteequipment/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
