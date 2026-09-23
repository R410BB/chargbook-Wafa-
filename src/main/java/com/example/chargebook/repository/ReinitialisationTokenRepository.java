package com.example.chargebook.repository;

import com.example.chargebook.model.ReinitialisationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ReinitialisationTokenRepository extends JpaRepository<ReinitialisationToken, UUID> {
    Optional<ReinitialisationToken> findByToken(String token);
}