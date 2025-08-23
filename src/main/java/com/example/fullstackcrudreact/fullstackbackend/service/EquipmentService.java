package com.example.fullstackcrudreact.fullstackbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullstackcrudreact.fullstackbackend.model.Equipment;
import com.example.fullstackcrudreact.fullstackbackend.repository.EquipmentRepository;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    // Get all equipment
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    // Get equipment by ID
    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    // Create new equipment
    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    // Update existing equipment
    public Optional<Equipment> updateEquipment(Long id, Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setName(updatedEquipment.getName());
            equipment.setCategory(updatedEquipment.getCategory());
            equipment.setBrand(updatedEquipment.getBrand());
            equipment.setYear(updatedEquipment.getYear());
            equipment.setAvailable(updatedEquipment.isAvailable());
            equipment.setConditionOfEquipment(updatedEquipment.getConditionOfEquipment());
            return equipmentRepository.save(equipment);
        });
    }

    // Delete equipment
    public boolean deleteEquipment(Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
