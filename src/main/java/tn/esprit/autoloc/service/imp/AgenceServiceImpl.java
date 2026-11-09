package tn.esprit.autoloc.service.imp;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceDTO;
import tn.esprit.autoloc.web.mapper.AgenceMapper;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.List;

@Service
@AllArgsConstructor
public class AgenceServiceImpl implements IAgenceService {

    private final AgenceRepository agenceRepository;
    private final AgenceMapper agenceMapper;

    @Override
    public List<AgenceDTO> retrieveAllAgences() {
        return agenceMapper.toDtoList(agenceRepository.findAll());
    }

    @Override
    public AgenceDTO addAgence(AgenceDTO agenceDTO) {
        Agence agence = agenceMapper.toEntity(agenceDTO);
        Agence saved = agenceRepository.save(agence);
        return agenceMapper.toDto(saved);
    }

    @Override
    public AgenceDTO updateAgence(AgenceDTO agenceDTO) {
        Agence agence = agenceMapper.toEntity(agenceDTO);
        Agence updated = agenceRepository.save(agence);
        return agenceMapper.toDto(updated);
    }

    @Override
    public AgenceDTO retrieveAgence(Long idAgence) {
        Agence agence = agenceRepository.findById(idAgence).orElse(null);
        return agenceMapper.toDto(agence);
    }

    @Override
    public void removeAgence(Long idAgence) {
        agenceRepository.deleteById(idAgence);
    }
}
