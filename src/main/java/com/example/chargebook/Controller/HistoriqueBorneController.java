package com.example.chargebook.Controller;

import com.example.chargebook.model.HistoriqueBorne;
import com.example.chargebook.repository.HistoriqueBorneRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historique-bornes")

public class HistoriqueBorneController {
    private final HistoriqueBorneRepository historiqueBorneRepository;
    public HistoriqueBorneController(HistoriqueBorneRepository historiqueBorneRepository) {
        this.historiqueBorneRepository = historiqueBorneRepository;
    }
    @GetMapping()
    public List<HistoriqueBorne> getAllHistoriqueBornes(){
        return historiqueBorneRepository.findAll();
    }
    @GetMapping("/{id}")
    public HistoriqueBorne getHistoriqueBorneById(@PathVariable UUID id){
        return (HistoriqueBorne) historiqueBorneRepository.findById(id).orElse(null);
    }
    @PostMapping
    public HistoriqueBorne createHistoriqueBorne(@RequestBody HistoriqueBorne historiqueBorne){
        return historiqueBorneRepository.save(historiqueBorne);
    }
    @PutMapping("/{id}")
    public HistoriqueBorne updateHistorique(@PathVariable UUID id, @RequestBody HistoriqueBorne h) {
        h.setIdIndisponibilite(id);
        return historiqueBorneRepository.save(h);
    }
}