package com.example.chargebook.repository;

import com.example.chargebook.model.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReclamationRepository extends JpaRepository<Reclamation, UUID> {
}