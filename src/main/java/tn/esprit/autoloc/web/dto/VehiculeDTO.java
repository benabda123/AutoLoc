package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeDTO {

    private Long idVehicule;

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    private String modele;

    @NotNull(message = "Le prix par jour est obligatoire")
    @Min(value = 1, message = "Le prix par jour doit être supérieur à 0")
    private Double prixParJour;

    @NotBlank(message = "Le statut est obligatoire")
    private String statut;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;
}