package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.service.IClientService;

import java.util.List;

@Tag(name = "Gestion des Clients", description = "API pour la gestion des clients d'AutoLoc")
@RestController
@RequestMapping("/client")
@AllArgsConstructor
public class ClientController {

    private final IClientService clientService;

    @Operation(summary = "Récupérer la liste complète des clients", description = "Retourne tous les clients enregistrés en base de données.")
    @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    @GetMapping("/retrieve-all-clients")
    public List<Client> getClients() {
        return clientService.retrieveAllClients();
    }

    @Operation(summary = "Récupérer un client par son ID", description = "Fournit un client spécifique à partir de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/retrieve-client/{client-id}")
    public Client retrieveClient(@PathVariable("client-id") Long clientId) {
        return clientService.retrieveClient(clientId);
    }

    @Operation(summary = "Ajouter un nouveau client", description = "Enregistre un nouveau client dans le système.")
    @ApiResponse(responseCode = "201", description = "Client créé avec succès")
    @PostMapping("/add-client")
    public Client addClient(@RequestBody Client c) {
        return clientService.addClient(c);
    }

    @Operation(summary = "Ajouter une liste de clients", description = "Enregistre plusieurs clients en une seule requête.")
    @ApiResponse(responseCode = "201", description = "Clients créés avec succès")
    @PostMapping("/add-clients")
    public List<Client> addClients(@RequestBody List<Client> clients) {
        return clientService.addClients(clients);
    }

    @Operation(summary = "Supprimer un client par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @DeleteMapping("/remove-client/{client-id}")
    public void removeClient(@PathVariable("client-id") Long clientId) {
        clientService.removeClient(clientId);
    }

    @Operation(summary = "Mettre à jour un client existant")
    @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès")
    @PutMapping("/update-client")
    public Client updateClient(@RequestBody Client c) {
        return clientService.updateClient(c);
    }
}