package com.example.chargebook.Controller;
import com.example.chargebook.model.Borne;
import com.example.chargebook.repository.BorneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/bornes")

public class BorneController {
    private final BorneRepository borneRepository;

    public BorneController(BorneRepository borneRepository){
        this.borneRepository =  borneRepository;
    }
    @GetMapping()

    public List<Borne> getallBornes(){
        
        return borneRepository.findAll();
    }
    @GetMapping("/{id}")
    public Borne getBorneById(@PathVariable UUID id){
        return borneRepository.findById(id).orElse(null);
    }
    @PostMapping
    public Borne createBorne(@RequestBody Borne borne){
        return borneRepository.save(borne);
    }
    @PutMapping("/{id}")
    public Borne updateBorne(@PathVariable UUID id, @RequestBody Borne borne) {
        borne.setIdBorne(id);
        return borneRepository.save(borne);
    }
    @DeleteMapping("/{id}")
    public void deleteBorne(@PathVariable UUID id) {
        borneRepository.deleteById(id);
    }
    }
    