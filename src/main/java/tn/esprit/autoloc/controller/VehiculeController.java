package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

import java.util.List;

@Tag(name = "Gestion des Véhicules", description = "API pour la gestion du parc automobile d'AutoLoc")
@RestController
@RequestMapping("/vehicule")
@AllArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Récupérer la liste des véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    @GetMapping("/retrieve-all-vehicules")
    public List<VehiculeDTO> getVehicules() {
        return vehiculeService.retrieveAllVehicules();
    }

    @Operation(summary = "Récupérer un véhicule par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/retrieve-vehicule/{vehicule-id}")
    public VehiculeDTO retrieveVehicule(@PathVariable("vehicule-id") Long vehiculeId) {
        return vehiculeService.retrieveVehicule(vehiculeId);
    }

    @Operation(summary = "Ajouter un nouveau véhicule")
    @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès")
    @PostMapping("/add-vehicule")
    public VehiculeDTO addVehicule(@Valid @RequestBody VehiculeDTO vehiculeDTO) {
        return vehiculeService.addVehicule(vehiculeDTO);
    }

    @Operation(summary = "Supprimer un véhicule par ID")
    @DeleteMapping("/remove-vehicule/{vehicule-id}")
    public void removeVehicule(@PathVariable("vehicule-id") Long vehiculeId) {
        vehiculeService.removeVehicule(vehiculeId);
    }

    @Operation(summary = "Mettre à jour les informations d'un véhicule")
    @ApiResponse(responseCode = "200", description = "Véhicule mis à jour avec succès")
    @PutMapping("/update-vehicule")
    public VehiculeDTO updateVehicule(@Valid @RequestBody VehiculeDTO vehiculeDTO) {
        return vehiculeService.updateVehicule(vehiculeDTO);
    }
}
