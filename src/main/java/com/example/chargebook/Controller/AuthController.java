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
import com.example.chargebook.model.ReinitialisationToken;
import com.example.chargebook.repository.ReinitialisationTokenRepository;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final InscriptionTokenRepository tokenRepository;
    private final EmailService emailService;
    private final ReinitialisationTokenRepository reinitialisationTokenRepository;

    public AuthController(UtilisateurRepository utilisateurRepository,
                        InscriptionTokenRepository tokenRepository,
                        ReinitialisationTokenRepository reinitialisationTokenRepository,
                        EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.tokenRepository = tokenRepository;
        this.reinitialisationTokenRepository = reinitialisationTokenRepository;
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
    // Règle du projet : le mot de passe doit contenir au moins 8 caractères,
    // appliquée à chaque point de création/modification de mot de passe.
    private static final int LONGUEUR_MIN_MOT_DE_PASSE = 8;

    private boolean motDePasseTropCourt(String motDePasse) {
        return motDePasse == null || motDePasse.length() < LONGUEUR_MIN_MOT_DE_PASSE;
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
        if (motDePasseTropCourt(body.get("motDePasse"))) {
            return Map.of("erreur", "Le mot de passe doit contenir au moins 8 caractères.");
        }

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
        if (motDePasseTropCourt(body.get("motDePasse"))) {
            return Map.of("erreur", "Le mot de passe doit contenir au moins 8 caractères.");
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
    @PostMapping("/mot-de-passe-oublie")
public Object motDePasseOublie(@RequestBody Map<String, String> body) {
    String email = body.get("email");

    // Message toujours identique, que l'email existe ou non : on ne veut pas
    // révéler quels emails ont un compte chez nous (sinon n'importe qui peut
    // tester des adresses et déduire qui est inscrit sur ChargeBook).
    String messageGenerique = "Si un compte existe avec cet email, un lien de réinitialisation a été envoyé.";

    if (email == null || email.isBlank()) {
        return Map.of("message", messageGenerique);
    }

    Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findAll().stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst();

    if (utilisateurOpt.isPresent()) {
        ReinitialisationToken token = new ReinitialisationToken();
        token.setEmail(email);
        token.setToken(UUID.randomUUID().toString());
        token.setDateExpiration(LocalDateTime.now().plusHours(1));
        reinitialisationTokenRepository.save(token);

        emailService.envoyerLienReinitialisation(email, token.getToken());
    }

    return Map.of("message", messageGenerique);
}

@GetMapping("/verifier-token-reinitialisation/{token}")
public Object verifierTokenReinitialisation(@PathVariable String token) {
    Optional<ReinitialisationToken> tokenOpt = reinitialisationTokenRepository.findByToken(token);
    if (tokenOpt.isEmpty()) {
        return Map.of("erreur", "Lien invalide.");
    }
    ReinitialisationToken t = tokenOpt.get();
    if (t.isUtilise()) {
        return Map.of("erreur", "Ce lien a déjà été utilisé.");
    }
    if (t.getDateExpiration().isBefore(LocalDateTime.now())) {
        return Map.of("erreur", "Ce lien a expiré.");
    }
    return Map.of("email", t.getEmail());
}

@PostMapping("/reinitialiser-mot-de-passe")
public Object reinitialiserMotDePasse(@RequestBody Map<String, String> body) {
    String token = body.get("token");
    String nouveauMotDePasse = body.get("motDePasse");

    Optional<ReinitialisationToken> tokenOpt = reinitialisationTokenRepository.findByToken(token);

    if (tokenOpt.isEmpty() || tokenOpt.get().isUtilise()
            || tokenOpt.get().getDateExpiration().isBefore(LocalDateTime.now())) {
        return Map.of("erreur", "Lien invalide ou expiré.");
    }

    if (motDePasseTropCourt(nouveauMotDePasse)) {
        return Map.of("erreur", "Le mot de passe doit contenir au moins 8 caractères.");
    }

    ReinitialisationToken t = tokenOpt.get();

    Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findAll().stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(t.getEmail()))
            .findFirst();

    if (utilisateurOpt.isEmpty()) {
        return Map.of("erreur", "Compte introuvable.");
    }

    Utilisateur utilisateur = utilisateurOpt.get();
    utilisateur.setMotDePasse(encoder.encode(nouveauMotDePasse));
    utilisateurRepository.save(utilisateur);

    t.setUtilise(true);
    reinitialisationTokenRepository.save(t);

    emailService.envoyerConfirmationReinitialisation(utilisateur.getEmail(), utilisateur.getPrenom());

    return Map.of("message", "Mot de passe modifié avec succès.");
}
}