package com.example.chargebook.repository;

import com.example.chargebook.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {
}