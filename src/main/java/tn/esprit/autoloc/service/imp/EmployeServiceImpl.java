package tn.esprit.autoloc.service.imp;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeDTO;
import tn.esprit.autoloc.web.mapper.EmployeMapper;
import tn.esprit.autoloc.repository.EmployeRepository;
import tn.esprit.autoloc.service.IEmployeService;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final EmployeRepository employeRepository;
    private final EmployeMapper employeMapper;

    @Override
    public List<EmployeDTO> retrieveAllEmployes() {
        return employeMapper.toDtoList(employeRepository.findAll());
    }

    @Override
    public EmployeDTO addEmploye(EmployeDTO employeDTO) {
        Employe employe = employeMapper.toEntity(employeDTO);
        Employe saved = employeRepository.save(employe);
        return employeMapper.toDto(saved);
    }

    @Override
    public EmployeDTO updateEmploye(EmployeDTO employeDTO) {
        Employe employe = employeMapper.toEntity(employeDTO);
        Employe updated = employeRepository.save(employe);
        return employeMapper.toDto(updated);
    }

    @Override
    public EmployeDTO retrieveEmploye(Long idEmploye) {
        Employe employe = employeRepository.findById(idEmploye).orElse(null);
        return employeMapper.toDto(employe);
    }

    @Override
    public void removeEmploye(Long idEmploye) {
        employeRepository.deleteById(idEmploye);
    }
}
