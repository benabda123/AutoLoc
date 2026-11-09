package tn.esprit.autoloc.service.imp;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.List;

@Service
@AllArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public List<VehiculeDTO> retrieveAllVehicules() {
        return vehiculeMapper.toDtoList(vehiculeRepository.findAll());
    }

    @Override
    public VehiculeDTO addVehicule(VehiculeDTO vehiculeDTO) {
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDTO);
        Vehicule saved = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(saved);
    }

    @Override
    public VehiculeDTO updateVehicule(VehiculeDTO vehiculeDTO) {
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDTO);
        Vehicule updated = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(updated);
    }

    @Override
    public VehiculeDTO retrieveVehicule(Long idVehicule) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule).orElse(null);
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    public void removeVehicule(Long idVehicule) {
        vehiculeRepository.deleteById(idVehicule);
    }
}
