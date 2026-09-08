package com.example.chargebook.repository;

import com.example.chargebook.model.Borne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BorneRepository extends JpaRepository<Borne, UUID> {
}