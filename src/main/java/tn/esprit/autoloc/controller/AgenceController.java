package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.web.dto.AgenceDTO;

import java.util.List;

@Tag(name = "Gestion des Agences", description = "API pour la gestion des agences physiques")
@RestController
@RequestMapping("/agence")
@AllArgsConstructor
public class AgenceController {

    private final IAgenceService agenceService;

    @Operation(summary = "Récupérer la liste des agences")
    @ApiResponse(responseCode = "200", description = "Liste des agences récupérée avec succès")
    @GetMapping("/retrieve-all-agences")
    public List<AgenceDTO> getAgences() {
        return agenceService.retrieveAllAgences();
    }

    @Operation(summary = "Récupérer une agence par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @GetMapping("/retrieve-agence/{agence-id}")
    public AgenceDTO retrieveAgence(@PathVariable("agence-id") Long agenceId) {
        return agenceService.retrieveAgence(agenceId);
    }

    @Operation(summary = "Créer une nouvelle agence")
    @ApiResponse(responseCode = "201", description = "Agence créée avec succès")
    @PostMapping("/add-agence")
    public AgenceDTO addAgence(@Valid @RequestBody AgenceDTO agenceDTO) {
        return agenceService.addAgence(agenceDTO);
    }

    @Operation(summary = "Supprimer une agence par ID")
    @DeleteMapping("/remove-agence/{agence-id}")
    public void removeAgence(@PathVariable("agence-id") Long agenceId) {
        agenceService.removeAgence(agenceId);
    }

    @Operation(summary = "Mettre à jour une agence")
    @ApiResponse(responseCode = "200", description = "Agence mise à jour avec succès")
    @PutMapping("/update-agence")
    public AgenceDTO updateAgence(@Valid @RequestBody AgenceDTO agenceDTO) {
        return agenceService.updateAgence(agenceDTO);
    }
}
