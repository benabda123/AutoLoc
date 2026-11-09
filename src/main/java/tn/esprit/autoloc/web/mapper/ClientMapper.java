package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.web.dto.ClientDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDto(Client client);
    Client toEntity(ClientDTO clientDTO);
    List<ClientDTO> toDtoList(List<Client> clients);
}
