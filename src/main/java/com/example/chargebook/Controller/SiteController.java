package com.example.chargebook.Controller;

import com.example.chargebook.model.Site;
import com.example.chargebook.repository.SiteRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sites")
public class SiteController {
    private final SiteRepository siteRepository;

    public SiteController(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @GetMapping
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    @PostMapping
    public Site createSite(@RequestBody Site site) {
        return siteRepository.save(site);
    }
    @PutMapping("/{id}")
    public Site updateSite(@PathVariable UUID id, @RequestBody Site site) {
        site.setIdSite(id);
        return siteRepository.save(site);
    }

    @DeleteMapping("/{id}")
    public void deleteSite(@PathVariable UUID id) {
        siteRepository.deleteById(id);
    }
}