package com.example.chargebook.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historique_borne")
public class HistoriqueBorne {

    @Id
    @GeneratedValue
    private UUID idIndisponibilite;

    @Column(nullable = false)
    private String motif;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = true)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private UUID idBorne;

    @Column(nullable = false)
    private UUID idDeclarePar;

    public UUID getIdIndisponibilite() { return idIndisponibilite; }
    public void setIdIndisponibilite(UUID idIndisponibilite) { this.idIndisponibilite = idIndisponibilite; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public UUID getIdBorne() { return idBorne; }
    public void setIdBorne(UUID idBorne) { this.idBorne = idBorne; }
    public UUID getIdDeclarePar() { return idDeclarePar; }
    public void setIdDeclarePar(UUID idDeclarePar) { this.idDeclarePar = idDeclarePar; }
}