package tn.esprit.autoloc.service.imp;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@Service
@AllArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final ContratRepository contratRepository;
    private final ContratMapper contratMapper;

    @Override
    public List<ContratDTO> retrieveAllContrats() {
        return contratMapper.toDtoList(contratRepository.findAll());
    }

    @Override
    public ContratDTO addContrat(ContratDTO contratDTO) {
        Contrat contrat = contratMapper.toEntity(contratDTO);
        Contrat saved = contratRepository.save(contrat);
        return contratMapper.toDto(saved);
    }

    @Override
    public ContratDTO updateContrat(ContratDTO contratDTO) {
        Contrat contrat = contratMapper.toEntity(contratDTO);
        Contrat updated = contratRepository.save(contrat);
        return contratMapper.toDto(updated);
    }

    @Override
    public ContratDTO retrieveContrat(Long idContrat) {
        Contrat contrat = contratRepository.findById(idContrat).orElse(null);
        return contratMapper.toDto(contrat);
    }

    @Override
    public void removeContrat(Long idContrat) {
        contratRepository.deleteById(idContrat);
    }
}
