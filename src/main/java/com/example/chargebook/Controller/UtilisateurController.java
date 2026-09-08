package com.example.chargebook.Controller;
import com.example.chargebook.model.Utilisateur;
import com.example.chargebook.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/utilisateurs")

public class UtilisateurController {
    private final UtilisateurRepository utilisateurRepository;
    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }
    @GetMapping()
    public List<Utilisateur> getAllUtilsateurs(){
        return utilisateurRepository.findAll();
    }

    @GetMapping("/{id}")
    public Utilisateur getUtilisateurById(@PathVariable UUID id){
        return utilisateurRepository.findById(id).orElse(null);
    }
    @PostMapping
    public Utilisateur createUtilisateur(@RequestBody Utilisateur utilisateur){
        return utilisateurRepository.save(utilisateur);
    }
    @PutMapping("/{id}")
    public Utilisateur updateUtilisateur(@PathVariable UUID id, @RequestBody Utilisateur utilisateur) {
        utilisateur.setIdUtilisateur(id);
        return utilisateurRepository.save(utilisateur);
    }
    @PutMapping("/{id}/bloquer")
    public Utilisateur toggleBloquer(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        Utilisateur u = utilisateurRepository.findById(id).orElseThrow();
        u.setBloque(body.get("bloque"));
        return utilisateurRepository.save(u);
    }
}
