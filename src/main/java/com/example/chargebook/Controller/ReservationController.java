package com.example.chargebook.Controller;

import com.example.chargebook.model.Reservation;
import com.example.chargebook.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import com.example.chargebook.model.Utilisateur;
import com.example.chargebook.repository.UtilisateurRepository;
import com.example.chargebook.service.EmailService;
import com.example.chargebook.model.Borne;
import com.example.chargebook.model.Site;
import com.example.chargebook.model.Vehicule;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.chargebook.repository.BorneRepository;
import com.example.chargebook.repository.SiteRepository;
import com.example.chargebook.repository.VehiculeRepository;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final BorneRepository borneRepository;
    private final SiteRepository siteRepository;
    private final EmailService emailService;

    public ReservationController(
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

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable UUID id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {

        if (reservation.getDateDebut() == null || reservation.getDateFin() == null
                || !reservation.getDateFin().isAfter(reservation.getDateDebut())) {
            return ResponseEntity.badRequest().body("Créneau invalide : la date de fin doit être après la date de début.");
        }

        if (dateHorsSemaineReservable(reservation)) {
            return ResponseEntity.status(409).body("Cette date n'est pas dans la semaine actuellement réservable.");
        }
        if (dateHorsSemaineReservable(reservation)) {
                return ResponseEntity.status(409).body("Cette date n'est pas dans la semaine actuellement réservable.");
        }

        if (dateDebutDejaPassee(reservation)) {
                return ResponseEntity.status(409).body("Ce créneau est déjà passé.");
        }

        if ("active".equalsIgnoreCase(reservation.getStatut()) && aUnChevauchement(reservation, null)) {
            return ResponseEntity.status(409).body("Ce créneau est déjà réservé sur cette borne.");
        }

        // 1. Enregistrer la réservation
        Reservation reservationEnregistree = reservationRepository.save(reservation);

        // 2. Récupérer l'utilisateur
        Utilisateur utilisateur =
                utilisateurRepository.findById(reservation.getIdUtilisateur())
                        .orElse(null);

        // 3. Récupérer le véhicule
        Vehicule vehicule =
                vehiculeRepository.findById(reservation.getIdVehicule())
                        .orElse(null);

        // 4. Récupérer la borne
        Borne borne =
                borneRepository.findById(reservation.getIdBorne())
                        .orElse(null);

        // 5. Récupérer le site
        Site site = null;

        if (borne != null) {
            site = siteRepository.findById(borne.getIdSite())
                    .orElse(null);
        }

        // 6. Envoyer l'e-mail
        if (utilisateur != null &&
            vehicule != null &&
            borne != null &&
            site != null) {

            emailService.envoyerConfirmationReservation(
                    utilisateur.getEmail(),
                    utilisateur.getPrenom(),
                    reservationEnregistree,
                    vehicule.getImmatriculation(),
                    borne.getNomBorne(),
                    site.getNom()
            );
        }

        return ResponseEntity.ok(reservationEnregistree);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable UUID id,
            @RequestBody Reservation reservation) {

        // Récupérer l'ancienne réservation
        Reservation ancienneReservation =
                reservationRepository.findById(id).orElse(null);

        if (ancienneReservation == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier si c'est une annulation
        boolean annulation =
                "annulee".equalsIgnoreCase(reservation.getStatut());

        // Vérifier si les dates ont changé
        boolean datesModifiees =
                !ancienneReservation.getDateDebut()
                        .equals(reservation.getDateDebut())
                ||
                !ancienneReservation.getDateFin()
                        .equals(reservation.getDateFin());

        // Anti-chevauchement + semaine réservable : uniquement si la réservation reste active
        // (pas de vérif utile si on est en train d'annuler)
        if (!annulation) {
                if (dateHorsSemaineReservable(reservation)) {
                        return ResponseEntity.status(409).body("Cette date n'est pas dans la semaine actuellement réservable.");
                }
                if (dateDebutDejaPassee(reservation)) {
                        return ResponseEntity.status(409).body("Ce créneau est déjà passé.");
                }
                if (aUnChevauchement(reservation, id)) {
                        return ResponseEntity.status(409).body("Ce créneau est déjà réservé sur cette borne.");
                }
        }

        // Enregistrer la nouvelle réservation
        reservation.setIdReservation(id);

        Reservation reservationModifiee =
                reservationRepository.save(reservation);

        // Récupérer l'utilisateur
        Utilisateur utilisateur =
                utilisateurRepository.findById(reservation.getIdUtilisateur())
                        .orElse(null);

        // Récupérer le véhicule
        Vehicule vehicule =
                vehiculeRepository.findById(reservation.getIdVehicule())
                        .orElse(null);

        // Récupérer la borne
        Borne borne =
                borneRepository.findById(reservation.getIdBorne())
                        .orElse(null);

        // Récupérer le site
        Site site = null;

        if (borne != null) {
            site = siteRepository.findById(borne.getIdSite())
                    .orElse(null);
        }

        // Envoyer les emails
        if (utilisateur != null &&
            vehicule != null &&
            borne != null &&
            site != null) {

        // ANNULATION
        if (annulation) {

        boolean pourIndisponibiliteBorne =
                "borne_indisponible".equals(reservation.getMotifAnnulation());

        if (pourIndisponibiliteBorne) {
                System.out.println("=== EMAIL ANNULATION (BORNE INDISPONIBLE) ===");
                emailService.envoyerAnnulationPourIndisponibiliteBorne(
                        utilisateur.getEmail(),
                        utilisateur.getPrenom(),
                        reservationModifiee,
                        vehicule.getImmatriculation(),
                        borne.getNomBorne(),
                        site.getNom()
                );
        } else {
                System.out.println("=== EMAIL ANNULATION ===");
                emailService.envoyerAnnulationReservation(
                        utilisateur.getEmail(),
                        utilisateur.getPrenom(),
                        reservationModifiee,
                        vehicule.getImmatriculation(),
                        borne.getNomBorne(),
                        site.getNom()
                );
        }
        }

            // MODIFICATION
            else if (datesModifiees) {

                System.out.println("=== EMAIL MODIFICATION ===");

                emailService.envoyerModificationReservation(
                        utilisateur.getEmail(),
                        utilisateur.getPrenom(),
                        reservationModifiee,
                        vehicule.getImmatriculation(),
                        borne.getNomBorne(),
                        site.getNom()
                );
            }
        }

        return ResponseEntity.ok(reservationModifiee);
    }

        private boolean aUnChevauchement(Reservation reservation, UUID idAExclure) {
                return !reservationRepository.findChevauchements(
                reservation.getIdBorne(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                idAExclure
                ).isEmpty();
        }
        // Reproduit côté serveur la même règle que getDebutSemaine() dans dateHelpers.js :
        // lundi-vendredi de la semaine en cours, sauf le week-end où on bascule
        // sur la semaine suivante.
        private LocalDate calculerDebutSemaineReservable() {
        LocalDate aujourdHui = LocalDate.now();
        DayOfWeek jour = aujourdHui.getDayOfWeek();

        if (jour == DayOfWeek.SATURDAY) {
                return aujourdHui.plusDays(2); // lundi prochain
        }
        if (jour == DayOfWeek.SUNDAY) {
                return aujourdHui.plusDays(1); // lundi prochain
        }
        // getValue() : lundi = 1 ... dimanche = 7
        return aujourdHui.minusDays(jour.getValue() - 1);
        }

        private boolean dateHorsSemaineReservable(Reservation reservation) {
        if (reservation.getDateDebut() == null) return true;

        LocalDate debutSemaine = calculerDebutSemaineReservable();
        LocalDate finSemaine = debutSemaine.plusDays(4); // vendredi
        LocalDate dateReservation = reservation.getDateDebut().toLocalDate();

        return dateReservation.isBefore(debutSemaine) || dateReservation.isAfter(finSemaine);
        }
        private boolean dateDebutDejaPassee(Reservation reservation) {
        if (reservation.getDateDebut() == null) return true;
        return reservation.getDateDebut().isBefore(LocalDateTime.now());
        }
}