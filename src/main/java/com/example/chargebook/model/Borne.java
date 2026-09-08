package com.example.chargebook.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "borne")
public class Borne {

    @Id
    @GeneratedValue
    private UUID idBorne;

    @Column(nullable = false)
    private String nomBorne;

    @Column(nullable = false)
    private String etat;

    @Column(nullable = false)
    private UUID idSite;

    public UUID getIdBorne() { return idBorne; }
    public void setIdBorne(UUID idBorne) { this.idBorne = idBorne; }
    public String getNomBorne() { return nomBorne; }
    public void setNomBorne(String nomBorne) { this.nomBorne = nomBorne; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public UUID getIdSite() { return idSite; }
    public void setIdSite(UUID idSite) { this.idSite = idSite; }
}