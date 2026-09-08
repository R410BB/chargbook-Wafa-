package com.example.chargebook.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue
    private UUID idSite;

    @Column(nullable = false)
    private String nom;

    @Column
    private String adresse;

    @Column(nullable = false)
    private boolean actif = true;

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public UUID getIdSite() { return idSite; }
    public void setIdSite(UUID idSite) { this.idSite = idSite; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}