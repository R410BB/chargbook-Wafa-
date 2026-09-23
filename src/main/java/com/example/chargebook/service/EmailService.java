package com.example.chargebook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.chargebook.model.Reservation;
import com.example.chargebook.model.Reclamation;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String expediteur;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Point d'entrée unique pour l'envoi : capture toute erreur mail pour
    // qu'elle ne fasse jamais planter l'action métier qui a déclenché le mail
    // (la réservation/le véhicule est déjà enregistré en base à ce stade).
    private void envoyerEnToutesSecurites(SimpleMailMessage message, String contexte) {
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("=== ÉCHEC ENVOI EMAIL (" + contexte + ") : " + e.getMessage() + " ===");
        }
    }

    public void envoyerLienInscription(String destinataire, String token) {

        System.out.println("=== ENVOI EMAIL ===");
        System.out.println("Destinataire : " + destinataire);
        System.out.println("Expéditeur : " + expediteur);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("ChargeBook — Finalisez votre inscription");
        message.setText(
            "Bonjour,\n\n" +
            "Cliquez sur le lien ci-dessous pour finaliser la création de votre compte ChargeBook :\n\n" +
            "http://localhost:3000/inscription?token=" + token + "\n\n" +
            "Ce lien expire dans 24 heures.\n\n" +
            "L'équipe ChargeBook"
        );

        envoyerEnToutesSecurites(message, "lien inscription");

        System.out.println("=== EMAIL ENVOYÉ (ou tentative loguée) ===");
    }

    public void envoyerBienvenue(String destinataire, String prenom) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("Bienvenue sur ChargeBook !");
        message.setText(
            "Bonjour " + prenom + ",\n\n" +
            "Votre compte ChargeBook a été créé avec succès.\n\n" +
            "Vous pouvez dès maintenant vous connecter sur l'application avec votre email et le mot de passe que vous venez de définir, " +
            "et réserver une borne de recharge en quelques clics.\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "bienvenue");
    }

    public void envoyerConfirmationReservation(
            String destinataire,
            String prenom,
            Reservation reservation,
            String immatriculation,
            String nomBorne,
            String nomSite) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(expediteur);
        message.setTo(destinataire);

        message.setSubject("ChargeBook — Confirmation de votre réservation");

        message.setText(
            "Bonjour " + prenom + ",\n\n" +

            "Votre réservation ChargeBook a bien été confirmée.\n\n" +

            "Détails de votre réservation\n\n" +

            "Date : " + reservation.getDateDebut().toLocalDate() + "\n" +
            "Horaire : " +
            reservation.getDateDebut().toLocalTime() +
            " - " +
            reservation.getDateFin().toLocalTime() + "\n" +

            "Site : " + nomSite + "\n" +
            "Borne : " + nomBorne + "\n" +
            "Véhicule : " + immatriculation + "\n\n" +

            "Merci d'utiliser ChargeBook.\n\n" +

            "L'équipe ChargeBook"
        );

        envoyerEnToutesSecurites(message, "confirmation réservation");
    }

    public void envoyerAnnulationReservation(
            String destinataire,
            String prenom,
            Reservation reservation,
            String immatriculation,
            String nomBorne,
            String nomSite) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(expediteur);
        message.setTo(destinataire);

        message.setSubject("ChargeBook — Annulation de votre réservation");

        message.setText(
            "Bonjour " + prenom + ",\n\n" +

            "Votre réservation ChargeBook a bien été annulée.\n\n" +

            "Détails de la réservation annulée\n\n" +

            "Date : " + reservation.getDateDebut().toLocalDate() + "\n" +
            "Horaire : " +
            reservation.getDateDebut().toLocalTime() +
            " - " +
            reservation.getDateFin().toLocalTime() + "\n" +

            "Site : " + nomSite + "\n" +
            "Borne : " + nomBorne + "\n" +
            "Véhicule : " + immatriculation + "\n\n" +

            "Merci d'utiliser ChargeBook.\n\n" +
            "L'équipe ChargeBook"
        );

        envoyerEnToutesSecurites(message, "annulation réservation");
    }

    public void envoyerModificationReservation(
            String destinataire,
            String prenom,
            Reservation reservation,
            String immatriculation,
            String nomBorne,
            String nomSite) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(expediteur);
        message.setTo(destinataire);

        message.setSubject("ChargeBook — Modification de votre réservation");

        message.setText(
            "Bonjour " + prenom + ",\n\n" +

            "Votre réservation ChargeBook a bien été modifiée.\n\n" +

            "Nouveaux détails de votre réservation\n\n" +

            "Date : " + reservation.getDateDebut().toLocalDate() + "\n" +
            "Horaire : " +
            reservation.getDateDebut().toLocalTime() +
            " - " +
            reservation.getDateFin().toLocalTime() + "\n" +

            "Site : " + nomSite + "\n" +
            "Borne : " + nomBorne + "\n" +
            "Véhicule : " + immatriculation + "\n\n" +

            "Merci d'utiliser ChargeBook.\n\n" +
            "L'équipe ChargeBook"
        );

        envoyerEnToutesSecurites(message, "modification réservation");
    }

    public void envoyerAnnulationPourIndisponibiliteBorne(
            String destinataire,
            String prenom,
            Reservation reservation,
            String immatriculation,
            String nomBorne,
            String nomSite) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(expediteur);
        message.setTo(destinataire);

        message.setSubject("ChargeBook — Votre réservation a été annulée (borne indisponible)");

        message.setText(
            "Bonjour " + prenom + ",\n\n" +

            "La borne que vous aviez réservée est tombée en panne ou a été mise en maintenance. " +
            "Votre réservation a donc dû être annulée automatiquement, indépendamment de votre volonté.\n\n" +

            "Réservation annulée\n\n" +

            "Date : " + reservation.getDateDebut().toLocalDate() + "\n" +
            "Horaire : " +
            reservation.getDateDebut().toLocalTime() +
            " - " +
            reservation.getDateFin().toLocalTime() + "\n" +

            "Site : " + nomSite + "\n" +
            "Borne : " + nomBorne + "\n" +
            "Véhicule : " + immatriculation + "\n\n" +

            "Vous pouvez reprogrammer un nouveau créneau directement depuis votre historique de réservations sur ChargeBook.\n\n" +

            "Désolé pour la gêne occasionnée.\n\n" +
            "L'équipe ChargeBook"
        );

        envoyerEnToutesSecurites(message, "annulation borne indisponible");
    }

    public void envoyerRappelDebutReservation(
            String destinataire,
            String prenom,
            Reservation reservation,
            String immatriculation,
            String nomBorne,
            String nomSite) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("ChargeBook — Votre réservation commence dans 15 min");
        message.setText(
            "Bonjour " + prenom + ",\n\n" +
            "Votre créneau de recharge commence dans 15 minutes.\n\n" +
            "Horaire : " + reservation.getDateDebut().toLocalTime() +
            " - " + reservation.getDateFin().toLocalTime() + "\n" +
            "Site : " + nomSite + "\n" +
            "Borne : " + nomBorne + "\n" +
            "Véhicule : " + immatriculation + "\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "rappel début");
    }

    public void envoyerRappelFinReservation(
            String destinataire,
            String prenom,
            Reservation reservation,
            String immatriculation,
            String nomBorne,
            String nomSite) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("ChargeBook — Votre réservation se termine dans 15 min");
        message.setText(
            "Bonjour " + prenom + ",\n\n" +
            "Votre créneau de recharge se termine dans 15 minutes. Pensez à libérer la borne.\n\n" +
            "Horaire : " + reservation.getDateDebut().toLocalTime() +
            " - " + reservation.getDateFin().toLocalTime() + "\n" +
            "Site : " + nomSite + "\n" +
            "Borne : " + nomBorne + "\n" +
            "Véhicule : " + immatriculation + "\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "rappel fin");
    }
    public void envoyerLienReinitialisation(String destinataire, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("ChargeBook — Réinitialisation de votre mot de passe");
        message.setText(
            "Bonjour,\n\n" +
            "Vous avez demandé la réinitialisation de votre mot de passe ChargeBook.\n\n" +
            "Cliquez sur le lien ci-dessous pour choisir un nouveau mot de passe :\n\n" +
            "http://localhost:3000/?resetToken=" + token + "\n\n" +
            "Ce lien expire dans 1 heure. Si vous n'êtes pas à l'origine de cette demande, ignorez simplement cet email.\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "lien réinitialisation");
    }

    public void envoyerConfirmationReinitialisation(String destinataire, String prenom) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("ChargeBook — Votre mot de passe a été modifié");
        message.setText(
            "Bonjour " + prenom + ",\n\n" +
            "Votre mot de passe ChargeBook vient d'être modifié avec succès.\n\n" +
            "Si vous n'êtes pas à l'origine de ce changement, contactez immédiatement l'administrateur.\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "confirmation réinitialisation");
    }
    public void envoyerReclamationTraitee(String destinataire, String prenom, Reclamation reclamation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("Votre réclamation a été résolue");
        message.setText(
            "Bonjour " + prenom + ",\n\n" +
            "Votre réclamation a bien été traitée par notre équipe.\n\n" +
            "Objet de la réclamation : " + reclamation.getType() + "\n" +
            "Statut final : Résolue\n" +
            (reclamation.getCommentaireAdmin() != null && !reclamation.getCommentaireAdmin().isBlank()
                ? "Commentaire de l'administrateur : " + reclamation.getCommentaireAdmin() + "\n\n"
                : "\n") +
            "Merci d'utiliser ChargeBook.\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "réclamation résolue");
    }

    public void envoyerReclamationRejetee(String destinataire, String prenom, Reclamation reclamation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(expediteur);
        message.setTo(destinataire);
        message.setSubject("Votre réclamation a été rejetée");
        message.setText(
            "Bonjour " + prenom + ",\n\n" +
            "Votre réclamation a été examinée par notre équipe et a été rejetée.\n\n" +
            "Objet de la réclamation : " + reclamation.getType() + "\n" +
            "Statut final : Rejetée\n" +
            (reclamation.getCommentaireAdmin() != null && !reclamation.getCommentaireAdmin().isBlank()
                ? "Motif : " + reclamation.getCommentaireAdmin() + "\n\n"
                : "\n") +
            "Merci d'utiliser ChargeBook.\n\n" +
            "L'équipe ChargeBook"
        );
        envoyerEnToutesSecurites(message, "réclamation rejetée");
    }
}