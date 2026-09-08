package com.example.chargebook.repository;

import com.example.chargebook.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SiteRepository extends JpaRepository<Site, UUID> {
}