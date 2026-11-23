package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

@Tag(name = "Réservations — Métier",
     description = "Opérations métier avancées : affectation client et création réservation+contrat")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationMetierController {

    private final IReservationService reservationService;

    @Operation(
        summary = "Affecter un client à une réservation existante",
        description = "Associe le client identifié par nom + prénom à la réservation spécifiée. "
                    + "Retourne 404 si la réservation ou le client est introuvable, "
                    + "409 si la réservation est ANNULEE/TERMINEE ou si plusieurs clients sont homonymes."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client affecté avec succès"),
        @ApiResponse(responseCode = "404", description = "Réservation ou client introuvable"),
        @ApiResponse(responseCode = "409", description = "Règle métier violée (statut ou homonymes)")
    })
    @PutMapping("/{idReservation}/client")
    public ResponseEntity<ReservationResponseDTO> affecterReservationAClient(
            @PathVariable Long idReservation,
            @RequestParam String nom,
            @RequestParam String prenom) {

        ReservationResponseDTO result = reservationService.affecterReservationAClient(idReservation, nom, prenom);
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Créer une réservation et son contrat associé en une seule transaction",
        description = "Crée simultanément une Reservation (statut CONFIRMEE) et un Contrat signé. "
                    + "Le montant total est calculé automatiquement : tarifJournalier × nombre de jours. "
                    + "Retourne 409 si le véhicule est indisponible ou si la période chevauche une réservation active."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Réservation et contrat créés avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou dates incohérentes"),
        @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable"),
        @ApiResponse(responseCode = "409", description = "Véhicule indisponible ou chevauchement de réservation")
    })
    @PostMapping("/avec-contrat")
    public ResponseEntity<ReservationResponseDTO> creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO requestDTO) {

        ReservationResponseDTO result = reservationService.creerReservationEtContratAssocie(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
