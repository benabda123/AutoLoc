package tn.esprit.autoloc.service.imp;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.web.dto.ClientDTO;
import tn.esprit.autoloc.web.mapper.ClientMapper;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.service.IClientService;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<ClientDTO> retrieveAllClients() {
        return clientMapper.toDtoList(clientRepository.findAll());
    }

    @Override
    public ClientDTO addClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client saved = clientRepository.save(client);
        return clientMapper.toDto(saved);
    }

    @Override
    public ClientDTO updateClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client updated = clientRepository.save(client);
        return clientMapper.toDto(updated);
    }

    @Override
    public ClientDTO retrieveClient(Long idClient) {
        Client client = clientRepository.findById(idClient).orElse(null);
        return clientMapper.toDto(client);
    }

    @Override
    public void removeClient(Long idClient) {
        clientRepository.deleteById(idClient);
    }
}
