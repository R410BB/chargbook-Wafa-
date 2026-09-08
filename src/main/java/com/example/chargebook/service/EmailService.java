package com.example.chargebook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String expediteur;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

    mailSender.send(message);

    System.out.println("=== EMAIL ENVOYÉ ===");
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
        mailSender.send(message);
    }
}