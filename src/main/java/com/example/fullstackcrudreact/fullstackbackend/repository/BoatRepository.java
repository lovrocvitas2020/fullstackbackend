package com.example.fullstackcrudreact.fullstackbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.Boat;

public interface BoatRepository  extends JpaRepository<Boat, Long> {
    List<Boat> findByAvailable(boolean available);
}


