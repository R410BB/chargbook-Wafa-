package com.example.chargebook.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vehicule")
public class Vehicule {

    @Id
    @GeneratedValue
    private UUID idVehicule;

    @Column(nullable = false)
    private String immatriculation;

    @Column(nullable = false)
    private UUID idUtilisateur;

    @Column
    private String marque;

     @Column
    private String modele;

    public UUID getIdVehicule() { return idVehicule; }
    public void setIdVehicule(UUID idVehicule) { this.idVehicule = idVehicule; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public UUID getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(UUID idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
}