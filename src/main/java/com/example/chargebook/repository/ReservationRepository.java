package com.example.chargebook.repository;

import com.example.chargebook.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    // Réservations actives sur la borne dont le créneau chevauche [dateDebut, dateFin]
    // idAExclure sert à ignorer la réservation qu'on est en train de modifier
    @Query("SELECT r FROM Reservation r WHERE r.idBorne = :idBorne " +
           "AND r.statut = 'active' " +
           "AND r.dateDebut < :dateFin AND r.dateFin > :dateDebut " +
           "AND (:idAExclure IS NULL OR r.idReservation <> :idAExclure)")
    List<Reservation> findChevauchements(
        @Param("idBorne") UUID idBorne,
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin,
        @Param("idAExclure") UUID idAExclure
    );
    @Query("SELECT r FROM Reservation r WHERE r.statut = 'active' " +
        "AND r.rappelDebutEnvoye = false " +
        "AND r.dateDebut > :maintenant AND r.dateDebut <= :borneSup")
    List<Reservation> findAReppelerAvantDebut(
        @Param("maintenant") LocalDateTime maintenant,
        @Param("borneSup") LocalDateTime borneSup
    );

    @Query("SELECT r FROM Reservation r WHERE r.statut = 'active' " +
        "AND r.rappelFinEnvoye = false " +
        "AND r.dateFin > :maintenant AND r.dateFin <= :borneSup")
    List<Reservation> findAReppelerAvantFin(
        @Param("maintenant") LocalDateTime maintenant,
        @Param("borneSup") LocalDateTime borneSup
    );
}