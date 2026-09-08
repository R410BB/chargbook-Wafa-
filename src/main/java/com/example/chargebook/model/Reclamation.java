package com.example.chargebook.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamation")
public class Reclamation {

    @Id
    @GeneratedValue
    private UUID idReclamation;

    @Column(nullable = false)
    private String type;

    @Column
    private String commentaire;

    @Column
    private UUID idReservation;

    @Column(nullable = false)
    private UUID idUtilisateurDeclarant;

    @Column
    private UUID idUtilisateurImpute;

    @Column
    private String immatContrevenant;

    @Column(nullable = false)
    private String statut;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(columnDefinition = "TEXT")
    private String commentaireAdmin;

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getCommentaireAdmin() { return commentaireAdmin; }
    public void setCommentaireAdmin(String commentaireAdmin) { this.commentaireAdmin = commentaireAdmin; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getIdReclamation() { return idReclamation; }
    public void setIdReclamation(UUID idReclamation) { this.idReclamation = idReclamation; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public UUID getIdReservation() { return idReservation; }
    public void setIdReservation(UUID idReservation) { this.idReservation = idReservation; }
    public UUID getIdUtilisateurDeclarant() { return idUtilisateurDeclarant; }
    public void setIdUtilisateurDeclarant(UUID idUtilisateurDeclarant) { this.idUtilisateurDeclarant = idUtilisateurDeclarant; }
    public UUID getIdUtilisateurImpute() { return idUtilisateurImpute; }
    public void setIdUtilisateurImpute(UUID idUtilisateurImpute) { this.idUtilisateurImpute = idUtilisateurImpute; }
    public String getImmatContrevenant() { return immatContrevenant; }
    public void setImmatContrevenant(String immatContrevenant) { this.immatContrevenant = immatContrevenant; }
}