package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.web.dto.ContratDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContratMapper {

    @Mapping(source = "reservation.idReservation", target = "reservationId")
    ContratDTO toDto(Contrat contrat);

    @Mapping(source = "reservationId", target = "reservation.idReservation")
    Contrat toEntity(ContratDTO contratDTO);

    List<ContratDTO> toDtoList(List<Contrat> contrats);
}
