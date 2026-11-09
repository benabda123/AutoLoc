package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {
    VehiculeDTO toDto(Vehicule vehicule);
    Vehicule toEntity(VehiculeDTO vehiculeDTO);
    List<VehiculeDTO> toDtoList(List<Vehicule> vehicules);
}
