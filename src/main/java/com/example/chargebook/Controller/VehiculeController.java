package com.example.chargebook.Controller;

import com.example.chargebook.model.Vehicule;
import com.example.chargebook.repository.VehiculeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {
    private final VehiculeRepository vehiculeRepository;
    public VehiculeController(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    @GetMapping()
    public List<Vehicule> getAllVehicules(){
        return vehiculeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Vehicule getVehiculeById(@PathVariable UUID id){
        return vehiculeRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> createVehicule(@RequestBody Vehicule vehicule){
        String plaque = normaliserPlaque(vehicule.getImmatriculation());
        vehicule.setImmatriculation(plaque);

        Optional<Vehicule> existant = vehiculeRepository.findByImmatriculationIgnoreCase(plaque);

        if (existant.isPresent()) {
            Vehicule vehiculeExistant = existant.get();

            // CAS 2 : la plaque existe déjà et est associée à un autre utilisateur -> refus.
            if (vehiculeExistant.getIdUtilisateur() != null) {
                return ResponseEntity.status(409).body("Ce véhicule est déjà associé à un utilisateur.");
            }

            // CAS 3 : la plaque existe mais le véhicule n'est associé à personne
            // (ex. dissocié par l'admin) -> on réutilise le véhicule existant,
            // on ne crée pas de doublon, on l'associe simplement au nouvel utilisateur.
            vehiculeExistant.setIdUtilisateur(vehicule.getIdUtilisateur());
            if (vehicule.getMarque() != null) {
                vehiculeExistant.setMarque(vehicule.getMarque());
            }
            if (vehicule.getModele() != null) {
                vehiculeExistant.setModele(vehicule.getModele());
            }
            return ResponseEntity.ok(vehiculeRepository.save(vehiculeExistant));
        }

        // CAS 1 : la plaque n'existe pas encore -> création normale.
        return ResponseEntity.ok(vehiculeRepository.save(vehicule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicule(@PathVariable UUID id){
        try {
            vehiculeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(
                "Impossible de supprimer ce véhicule : il est lié à des réservations existantes. " +
                "Dissociez-le plutôt de son utilisateur si besoin."
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicule(@PathVariable UUID id, @RequestBody Vehicule vehicule) {
        String plaque = normaliserPlaque(vehicule.getImmatriculation());
        vehicule.setImmatriculation(plaque);
        vehicule.setIdVehicule(id);

        Optional<Vehicule> existant = vehiculeRepository.findByImmatriculationIgnoreCase(plaque);
        if (existant.isPresent() && !existant.get().getIdVehicule().equals(id)) {
            return ResponseEntity.status(409).body("Cette plaque d'immatriculation est déjà enregistrée sur un autre véhicule.");
        }
        return ResponseEntity.ok(vehiculeRepository.save(vehicule));
    }

    private String normaliserPlaque(String immatriculation) {
        if (immatriculation == null) return null;
        return immatriculation.trim().toUpperCase().replaceAll("\\s+", "");
    }
    // Dissocie un véhicule de son utilisateur (action admin). Le véhicule,
    // sa plaque, ses autres informations et l'historique de ses réservations
    // restent inchangés : seule l'association avec l'utilisateur est retirée.
    @PutMapping("/{id}/dissocier")
    public ResponseEntity<?> dissocierVehicule(@PathVariable UUID id) {
        Optional<Vehicule> vehiculeOpt = vehiculeRepository.findById(id);
        if (vehiculeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Véhicule introuvable.");
        }

        Vehicule vehicule = vehiculeOpt.get();
        if (vehicule.getIdUtilisateur() == null) {
            return ResponseEntity.status(409).body("Ce véhicule n'est associé à aucun utilisateur.");
        }

        vehicule.setIdUtilisateur(null);
        return ResponseEntity.ok(vehiculeRepository.save(vehicule));
    }
}