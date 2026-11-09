package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContratDTO {

    private Long idContrat;

    @NotNull(message = "La date de signature est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total est obligatoire")
    private BigDecimal montantTotal;

    private Boolean valide;

    private Long reservationId;
}
