package com.example.chargebook.repository;

import com.example.chargebook.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VehiculeRepository extends JpaRepository<Vehicule, UUID> {
    Optional<Vehicule> findByImmatriculationIgnoreCase(String immatriculation);
}