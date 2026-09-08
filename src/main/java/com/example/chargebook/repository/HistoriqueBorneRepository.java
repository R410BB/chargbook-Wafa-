package com.example.chargebook.repository;

import com.example.chargebook.model.HistoriqueBorne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface HistoriqueBorneRepository extends JpaRepository<HistoriqueBorne, UUID> {
}