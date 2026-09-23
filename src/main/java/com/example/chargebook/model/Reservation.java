package com.example.chargebook.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue
    private UUID idReservation;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private String statut;

    @Column
    private String motifAnnulation;

    @Column(nullable = false)
    private UUID idUtilisateur;

    @Column(nullable = false)
    private UUID idBorne;

    @Column(nullable = false)
    private UUID idVehicule;

    @Column(nullable = false)
    private boolean rappelDebutEnvoye = false;

    @Column(nullable = false)
    private boolean rappelFinEnvoye = false;

    public boolean isRappelDebutEnvoye() { return rappelDebutEnvoye; }
    public void setRappelDebutEnvoye(boolean rappelDebutEnvoye) { this.rappelDebutEnvoye = rappelDebutEnvoye; }
    public boolean isRappelFinEnvoye() { return rappelFinEnvoye; }
    public void setRappelFinEnvoye(boolean rappelFinEnvoye) { this.rappelFinEnvoye = rappelFinEnvoye; }
    public UUID getIdReservation() { return idReservation; }
    public void setIdReservation(UUID idReservation) { this.idReservation = idReservation; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(UUID idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public UUID getIdBorne() { return idBorne; }
    public void setIdBorne(UUID idBorne) { this.idBorne = idBorne; }
    public UUID getIdVehicule() { return idVehicule; }
    public void setIdVehicule(UUID idVehicule) { this.idVehicule = idVehicule; }
}