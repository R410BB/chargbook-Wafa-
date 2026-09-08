package com.example.chargebook.Controller;

import com.example.chargebook.model.Vehicule;
import com.example.chargebook.repository.VehiculeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicules")

public class VehiculeController {
    private final VehiculeRepository vehiculeRepository;
    public VehiculeController(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }
    @GetMapping()
    public List<Vehicule> getAllVehicules(){
        return vehiculeRepository.findAll();
    }
    @GetMapping("/{id}")
    public Vehicule getVehiculeById(@PathVariable UUID id){
        return vehiculeRepository.findById(id).orElse(null);
    }
    @PostMapping
    public Vehicule createVehicule(@RequestBody Vehicule vehicule){
        return vehiculeRepository.save(vehicule);
    }
    @DeleteMapping("/{id}")
    public void deleteVehicule(@PathVariable UUID id){
        vehiculeRepository.deleteById(id);
    }
    @PutMapping("/{id}")
    public Vehicule updateVehicule(@PathVariable UUID id, @RequestBody Vehicule vehicule) {
        vehicule.setIdVehicule(id);
        return vehiculeRepository.save(vehicule);
    }
}
