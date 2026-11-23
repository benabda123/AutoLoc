package tn.esprit.autoloc.web.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationResponseDTO {

    private Long idReservation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut;

    // Client
    private Long idClient;
    private String nomClient;
    private String prenomClient;

    // Véhicule
    private Long idVehicule;
    private String immatriculation;

    // Contrat associé (null si aucun contrat)
    private ContratDTO contrat;
}
