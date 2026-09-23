package com.example.chargebook.Controller;
import com.example.chargebook.model.Reclamation;
import com.example.chargebook.model.Utilisateur;
import com.example.chargebook.repository.ReclamationRepository;
import com.example.chargebook.repository.UtilisateurRepository;
import com.example.chargebook.service.EmailService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reclamations")

public class ReclamationController {
    private final ReclamationRepository reclamationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;

    public ReclamationController(ReclamationRepository reclamationRepository,
                                  UtilisateurRepository utilisateurRepository,
                                  EmailService emailService) {
        this.reclamationRepository = reclamationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
    }

    @GetMapping()
    public List<Reclamation> getAllReclamations(){
        return reclamationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Reclamation getReclamationById(@PathVariable UUID id){
        return reclamationRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Reclamation createReclamation(@RequestBody Reclamation reclamation){
        return reclamationRepository.save(reclamation);
    }

    @PutMapping("/{id}")
    public Reclamation updateReclamation(@PathVariable UUID id, @RequestBody Reclamation reclamation) {
        // On récupère l'état actuel AVANT d'écraser, uniquement pour savoir si le
        // statut change réellement (et donc si un email doit partir). On ne veut
        // pas renvoyer le même email si l'admin modifie la réclamation sans changer
        // son statut (ex: juste le commentaire).
        Reclamation ancien = reclamationRepository.findById(id).orElse(null);
        String ancienStatut = (ancien != null) ? ancien.getStatut() : null;

        reclamation.setIdReclamation(id);
        Reclamation enregistree = reclamationRepository.save(reclamation);

        String nouveauStatut = enregistree.getStatut();
        boolean statutAChange = nouveauStatut != null && !nouveauStatut.equals(ancienStatut);

        if (statutAChange && ("traitee".equals(nouveauStatut) || "rejetee".equals(nouveauStatut))) {
            envoyerEmailStatutSiPossible(enregistree, nouveauStatut);
        }

        return enregistree;
    }

    // L'envoi de l'email ne doit jamais faire échouer la requête : le statut
    // est déjà enregistré en base au moment où on arrive ici.
    private void envoyerEmailStatutSiPossible(Reclamation reclamation, String nouveauStatut) {
        try {
            Utilisateur utilisateur = utilisateurRepository
                    .findById(reclamation.getIdUtilisateurDeclarant())
                    .orElse(null);

            if (utilisateur == null) {
                System.err.println("=== IMPOSSIBLE D'ENVOYER L'EMAIL RÉCLAMATION : utilisateur déclarant introuvable (id="
                        + reclamation.getIdUtilisateurDeclarant() + ") ===");
                return;
            }

            if ("traitee".equals(nouveauStatut)) {
                emailService.envoyerReclamationTraitee(utilisateur.getEmail(), utilisateur.getPrenom(), reclamation);
            } else if ("rejetee".equals(nouveauStatut)) {
                emailService.envoyerReclamationRejetee(utilisateur.getEmail(), utilisateur.getPrenom(), reclamation);
            }
        } catch (Exception e) {
            System.err.println("=== ÉCHEC ENVOI EMAIL RÉCLAMATION (id=" + reclamation.getIdReclamation() + ") : "
                    + e.getMessage() + " ===");
        }
    }
}