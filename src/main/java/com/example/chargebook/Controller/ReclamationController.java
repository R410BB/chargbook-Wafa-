package com.example.chargebook.Controller;
import com.example.chargebook.model.Reclamation;
import com.example.chargebook.repository.ReclamationRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reclamations")

public class ReclamationController {
    private final ReclamationRepository reclamationRepository;

    public ReclamationController(ReclamationRepository reclamationRepository) {
        this.reclamationRepository = reclamationRepository;
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
        reclamation.setIdReclamation(id);
        return reclamationRepository.save(reclamation);
}
}
