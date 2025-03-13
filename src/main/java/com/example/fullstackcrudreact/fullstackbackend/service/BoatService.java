package com.example.fullstackcrudreact.fullstackbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullstackcrudreact.fullstackbackend.model.Boat;
import com.example.fullstackcrudreact.fullstackbackend.repository.BoatRepository;

@Service
public class BoatService {

     @Autowired
    private BoatRepository boatRepository;

    public List<Boat> getAllBoats() {
        return boatRepository.findAll();
    }

    public Optional<Boat> getBoatById(Long id) {
        return boatRepository.findById(id);
    }

    public Boat addBoat(Boat boat) {
        return boatRepository.save(boat);
    }

    public Boat updateBoat(Long id, Boat newBoat) {
        return boatRepository.findById(id).map(boat -> {
            boat.setName(newBoat.getName());
            boat.setType(newBoat.getType());
            boat.setSeats(newBoat.getSeats());
            boat.setMaterial(newBoat.getMaterial());
            boat.setYear(newBoat.getYear());
            boat.setBoatCondition(newBoat.getBoatCondition());
            boat.setAvailable(newBoat.isAvailable());
            return boatRepository.save(boat);
        }).orElseThrow(() -> new RuntimeException("Boat not found"));
    }

    public void deleteBoat(Long id) {
        boatRepository.deleteById(id);
    }

}
