package com.example.chargebook.Controller;

import com.example.chargebook.model.Utilisateur;
import com.example.chargebook.repository.UtilisateurRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import com.example.chargebook.model.InscriptionToken;
import com.example.chargebook.repository.InscriptionTokenRepository;
import com.example.chargebook.service.EmailService;
import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final InscriptionTokenRepository tokenRepository;
    private final EmailService emailService;

    public AuthController(UtilisateurRepository utilisateurRepository, 
                        InscriptionTokenRepository tokenRepository, 
                        EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String motDePasse = body.get("motDePasse");

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();

        // Utilisateur introuvable
        if (utilisateurOpt.isEmpty()) {
            return Map.of("erreur", "Email ou mot de passe incorrect");
        }

        // On récupère l'utilisateur depuis l'Optional
        Utilisateur utilisateur = utilisateurOpt.get();

        // Vérification si le compte est bloqué
        if (utilisateur.isBloque()) {
            return Map.of(
                    "erreur",
                    "Ce compte a été bloqué. Contactez l'administrateur."
            );
        }

        // Vérification du mot de passe
        if (!encoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            return Map.of("erreur", "Email ou mot de passe incorrect");
        }

        // Connexion réussie
        return Map.of(
                "idUtilisateur", utilisateur.getIdUtilisateur(),
                "nom", utilisateur.getNom(),
                "prenom", utilisateur.getPrenom(),
                "email", utilisateur.getEmail(),
                "role", utilisateur.getRole()
        );
    }
    @PostMapping("/register")
    public Object register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        
        boolean existeDeja = utilisateurRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        
        if (existeDeja) {
            return Map.of("erreur", "Un compte existe déjà avec cet email.");
        }

        Utilisateur u = new Utilisateur();
        u.setNom(body.get("nom"));
        u.setPrenom(body.get("prenom"));
        u.setEmail(email);
        u.setMotDePasse(encoder.encode(body.get("motDePasse")));
        u.setRole(body.getOrDefault("role", "utilisateur"));
        utilisateurRepository.save(u);
        return Map.of(
            "idUtilisateur", u.getIdUtilisateur(),
            "nom", u.getNom(),
            "prenom", u.getPrenom(),
            "email", u.getEmail(),
            "role", u.getRole()
        );
    }
    @PostMapping("/demande-inscription")
    public Object demanderInscription(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return Map.of("erreur", "L'adresse email est requise.");
        }

        boolean existeDeja = utilisateurRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (existeDeja) {
            return Map.of("erreur", "Un compte existe déjà avec cet email.");
        }

        InscriptionToken token = new InscriptionToken();
        token.setEmail(email);
        token.setToken(UUID.randomUUID().toString());
        token.setDateExpiration(LocalDateTime.now().plusHours(24));
        tokenRepository.save(token);

        emailService.envoyerLienInscription(email, token.getToken());

        return Map.of("message", "Un email de confirmation vous a été envoyé.");
    }

    @GetMapping("/verifier-token/{token}")
    public Object verifierToken(@PathVariable String token) {
        Optional<InscriptionToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return Map.of("erreur", "Lien invalide.");
        }
        InscriptionToken t = tokenOpt.get();
        if (t.isUtilise()) {
            return Map.of("erreur", "Ce lien a déjà été utilisé.");
        }
        if (t.getDateExpiration().isBefore(LocalDateTime.now())) {
            return Map.of("erreur", "Ce lien a expiré.");
        }
        return Map.of("email", t.getEmail());
    }

    @PostMapping("/finaliser-inscription")
    public Object finaliserInscription(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        Optional<InscriptionToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isUtilise() || tokenOpt.get().getDateExpiration().isBefore(LocalDateTime.now())) {
            return Map.of("erreur", "Lien invalide ou expiré.");
        }

        InscriptionToken t = tokenOpt.get();

        Utilisateur u = new Utilisateur();
        u.setNom(body.get("nom"));
        u.setPrenom(body.get("prenom"));
        u.setEmail(t.getEmail());
        u.setMotDePasse(encoder.encode(body.get("motDePasse")));
        u.setRole("utilisateur");
        u.setBloque(false);
        utilisateurRepository.save(u);

        t.setUtilise(true);
        tokenRepository.save(t);

        emailService.envoyerBienvenue(t.getEmail(), u.getPrenom());

        return Map.of(
            "idUtilisateur", u.getIdUtilisateur(),
            "nom", u.getNom(),
            "prenom", u.getPrenom(),
            "email", u.getEmail(),
            "role", u.getRole()
        );
    }
}