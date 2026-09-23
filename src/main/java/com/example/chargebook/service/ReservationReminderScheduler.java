package com.example.chargebook.service;

import com.example.chargebook.model.Borne;
import com.example.chargebook.model.Reservation;
import com.example.chargebook.model.Site;
import com.example.chargebook.model.Utilisateur;
import com.example.chargebook.model.Vehicule;
import com.example.chargebook.repository.BorneRepository;
import com.example.chargebook.repository.ReservationRepository;
import com.example.chargebook.repository.SiteRepository;
import com.example.chargebook.repository.UtilisateurRepository;
import com.example.chargebook.repository.VehiculeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationReminderScheduler {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final BorneRepository borneRepository;
    private final SiteRepository siteRepository;
    private final EmailService emailService;

    public ReservationReminderScheduler(
            ReservationRepository reservationRepository,
            UtilisateurRepository utilisateurRepository,
            VehiculeRepository vehiculeRepository,
            BorneRepository borneRepository,
            SiteRepository siteRepository,
            EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.borneRepository = borneRepository;
        this.siteRepository = siteRepository;
        this.emailService = emailService;
    }

    // Fréquence demandée par l'encadrant : expression cron, ~45 minutes.
    //
    // IMPORTANT — comportement réel de "0 */45 * * * *" dans Spring :
    // le champ "minutes" va de 0 à 59, donc "*/45" ne représente PAS un
    // intervalle de 45 minutes : il désigne la liste {0, 45}. Le job se
    // déclenche donc à xx:00 et xx:45 de chaque heure (cadence irrégulière :
    // 45 min puis 15 min en alternance). Il n'existe pas d'expression cron
    // classique pour un vrai intervalle strict de 45 minutes (45 ne divise
    // pas 60) ; c'est la solution la plus proche exprimable en cron.
    //
    // La fenêtre de détection reste volontairement à 15 minutes (pas
    // élargie) : dans ChargeBook, toutes les réservations sont créées via
    // calculerDatesDepuisDate() qui force les créneaux à démarrer et finir
    // pile à l'heure ronde (xx:00). L'instant idéal du rappel (début - 15min)
    // tombe donc toujours exactement sur xx:45, qui coïncide avec l'une des
    // deux exécutions du cron : aucune réservation ne peut passer "entre
    // deux" tant que cette contrainte métier (créneaux à l'heure ronde) est
    // respectée par tous les points de création de réservation.
    @Scheduled(cron = "0 */45 * * * *")
    public void verifierRappels() {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dans15min = maintenant.plusMinutes(15);

        // Rappels "ça commence dans 15 min"
        List<Reservation> aRappelerDebut =
                reservationRepository.findAReppelerAvantDebut(maintenant, dans15min);
        for (Reservation r : aRappelerDebut) {
            envoyerSiPossible(r, true);
            r.setRappelDebutEnvoye(true);
            reservationRepository.save(r);
        }

        // Rappels "ça se termine dans 15 min"
        List<Reservation> aRappelerFin =
                reservationRepository.findAReppelerAvantFin(maintenant, dans15min);
        for (Reservation r : aRappelerFin) {
            envoyerSiPossible(r, false);
            r.setRappelFinEnvoye(true);
            reservationRepository.save(r);
        }
    }

    private void envoyerSiPossible(Reservation reservation, boolean estDebut) {
        Utilisateur utilisateur = utilisateurRepository.findById(reservation.getIdUtilisateur()).orElse(null);
        Vehicule vehicule = vehiculeRepository.findById(reservation.getIdVehicule()).orElse(null);
        Borne borne = borneRepository.findById(reservation.getIdBorne()).orElse(null);
        Site site = (borne != null) ? siteRepository.findById(borne.getIdSite()).orElse(null) : null;

        if (utilisateur == null || vehicule == null || borne == null || site == null) {
            return;
        }

        if (estDebut) {
            emailService.envoyerRappelDebutReservation(
                utilisateur.getEmail(), utilisateur.getPrenom(), reservation,
                vehicule.getImmatriculation(), borne.getNomBorne(), site.getNom()
            );
        } else {
            emailService.envoyerRappelFinReservation(
                utilisateur.getEmail(), utilisateur.getPrenom(), reservation,
                vehicule.getImmatriculation(), borne.getNomBorne(), site.getNom()
            );
        }
    }
}