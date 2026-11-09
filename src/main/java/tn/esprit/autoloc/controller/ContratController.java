package tn.esprit.autoloc.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.web.dto.ContratDTO;

import java.util.List;

@RestController
@RequestMapping("/contrat")
@AllArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @GetMapping("/retrieve-all-contrats")
    public List<ContratDTO> getContrats() {
        return contratService.retrieveAllContrats();
    }

    @GetMapping("/retrieve-contrat/{contrat-id}")
    public ContratDTO retrieveContrat(@PathVariable("contrat-id") Long contratId) {
        return contratService.retrieveContrat(contratId);
    }

    @PostMapping("/add-contrat")
    public ContratDTO addContrat(@Valid @RequestBody ContratDTO contratDTO) {
        return contratService.addContrat(contratDTO);
    }

    @DeleteMapping("/remove-contrat/{contrat-id}")
    public void removeContrat(@PathVariable("contrat-id") Long contratId) {
        contratService.removeContrat(contratId);
    }

    @PutMapping("/update-contrat")
    public ContratDTO updateContrat(@Valid @RequestBody ContratDTO contratDTO) {
        return contratService.updateContrat(contratDTO);
    }
}
