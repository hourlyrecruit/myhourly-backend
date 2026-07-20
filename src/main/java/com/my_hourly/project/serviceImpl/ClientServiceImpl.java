package com.my_hourly.project.serviceImpl;

import com.my_hourly.project.dto.ClientRequest;
import com.my_hourly.project.dto.ClientResponse;
import com.my_hourly.project.entity.Client;
import com.my_hourly.project.repository.ClientRepository;
import com.my_hourly.project.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Override
    public ClientResponse createClient(ClientRequest request) {
        if(clientRepository.existsByClientCode(request.getClientCode())){
            throw new RuntimeException("Client code already exists.");
        }
        if(request.getEmail() != null && clientRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists.");
        }
        Client client = new Client();
        client.setClientCode(request.getClientCode());
        client.setCompanyName(request.getCompanyName());
        client.setContactPerson(request.getContactPerson());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setStatus(request.getStatus());
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

       Client savedClient = clientRepository.save(client);
       return mapToResponse(savedClient);
    }

    @Override
    public ClientResponse updateClient(Long clientId, ClientRequest request) {
        Client client = clientRepository.findById(clientId).
                orElseThrow(()->new RuntimeException("Client not found."));
        if(!client.getClientCode().equals(request.getClientCode()) && clientRepository
                .existsByClientCode(request.getClientCode())) {
            throw new RuntimeException("Company Code already exists.");
        }
        if (request.getEmail() != null
                    && !request.getEmail().equals(client.getEmail())
                    && clientRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists.");

        }
        client.setClientCode(request.getClientCode());
        client.setCompanyName(request.getCompanyName());
        client.setContactPerson(request.getContactPerson());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setStatus(request.getStatus());
        client.setUpdatedAt(LocalDateTime.now());

        Client updatedClient = clientRepository.save(client);

        return mapToResponse(updatedClient);
    }

    @Override
    public String deleteClient(Long clientId) {
       Client client =  clientRepository.findById(clientId)
               .orElseThrow(()->new RuntimeException("Client not found."));
       clientRepository.delete(client);
        return "Deleted Successfully.";
    }

    @Override
    public ClientResponse getClientById(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(()->new RuntimeException("Client not found."));
        return mapToResponse(client);
    }

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public ClientResponse patchClient(Long clientId, ClientRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        if (request.getClientCode() != null) {
            if (!request.getClientCode().equals(client.getClientCode())
                    && clientRepository.existsByClientCode(request.getClientCode())) {
                throw new RuntimeException("Client code already exists.");
            }
            client.setClientCode(request.getClientCode());
        }
        if (request.getCompanyName() != null) {
            client.setCompanyName(request.getCompanyName());
        }
        if (request.getContactPerson() != null) {
            client.setContactPerson(request.getContactPerson());
        }
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(client.getEmail())
                    && clientRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists.");
            }
            client.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            client.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            client.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            client.setStatus(request.getStatus());
        }
        client.setUpdatedAt(LocalDateTime.now());
        Client updatedClient = clientRepository.save(client);
        return mapToResponse(updatedClient);
    }
    private ClientResponse mapToResponse(Client client) {

        ClientResponse response = new ClientResponse();

        response.setId(client.getId());
        response.setClientCode(client.getClientCode());
        response.setCompanyName(client.getCompanyName());
        response.setContactPerson(client.getContactPerson());
        response.setEmail(client.getEmail());
        response.setPhone(client.getPhone());
        response.setAddress(client.getAddress());
        response.setStatus(client.getStatus());

        return response;
    }
}
