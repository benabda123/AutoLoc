package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeMapper {

    @Mapping(source = "agence.idAgence", target = "agenceId")
    @Mapping(source = "role", target = "role")
    EmployeDTO toDto(Employe employe);

    @Mapping(source = "agenceId", target = "agence.idAgence")
    @Mapping(source = "role", target = "role")
    Employe toEntity(EmployeDTO employeDTO);

    List<EmployeDTO> toDtoList(List<Employe> employes);
}
