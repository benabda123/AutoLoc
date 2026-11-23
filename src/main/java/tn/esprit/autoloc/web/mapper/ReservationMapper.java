package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;

@Mapper(componentModel = "spring", uses = {ContratMapper.class})
public interface ReservationMapper {

    @Mapping(source = "client.idClient",   target = "idClient")
    @Mapping(source = "client.nom",        target = "nomClient")
    @Mapping(source = "client.prenom",     target = "prenomClient")
    @Mapping(source = "vehicule.idVehicule",      target = "idVehicule")
    @Mapping(source = "vehicule.immatriculation", target = "immatriculation")
    @Mapping(source = "statut",            target = "statut")
    @Mapping(source = "contrat",           target = "contrat")
    ReservationResponseDTO toResponseDto(Reservation reservation);
}
