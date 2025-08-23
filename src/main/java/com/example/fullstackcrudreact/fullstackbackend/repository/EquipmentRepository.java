package com.example.fullstackcrudreact.fullstackbackend.repository;

import com.example.fullstackcrudreact.fullstackbackend.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    // Custom query methods can go here if needed, e.g.:
    // List<Equipment> findByAvailableTrue();
}
