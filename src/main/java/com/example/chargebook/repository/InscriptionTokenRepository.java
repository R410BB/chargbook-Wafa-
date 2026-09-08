package com.example.chargebook.repository;

import com.example.chargebook.model.InscriptionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InscriptionTokenRepository extends JpaRepository<InscriptionToken, UUID> {
    Optional<InscriptionToken> findByToken(String token);
}