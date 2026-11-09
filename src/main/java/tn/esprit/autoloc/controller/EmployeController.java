package tn.esprit.autoloc.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.web.dto.EmployeDTO;

import java.util.List;

@RestController
@RequestMapping("/employe")
@AllArgsConstructor
public class EmployeController {

    private final IEmployeService employeService;

    @GetMapping("/retrieve-all-employes")
    public List<EmployeDTO> getEmployes() {
        return employeService.retrieveAllEmployes();
    }

    @GetMapping("/retrieve-employe/{employe-id}")
    public EmployeDTO retrieveEmploye(@PathVariable("employe-id") Long employeId) {
        return employeService.retrieveEmploye(employeId);
    }

    @PostMapping("/add-employe")
    public EmployeDTO addEmploye(@Valid @RequestBody EmployeDTO employeDTO) {
        return employeService.addEmploye(employeDTO);
    }

    @DeleteMapping("/remove-employe/{employe-id}")
    public void removeEmploye(@PathVariable("employe-id") Long employeId) {
        employeService.removeEmploye(employeId);
    }

    @PutMapping("/update-employe")
    public EmployeDTO updateEmploye(@Valid @RequestBody EmployeDTO employeDTO) {
        return employeService.updateEmploye(employeDTO);
    }
}
