package com.my_hourly.project.service;

import com.my_hourly.project.dto.ClientRequest;
import com.my_hourly.project.dto.ClientResponse;
import com.my_hourly.project.entity.ClientStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ClientService {
    ClientResponse createClient(ClientRequest request);

    ClientResponse updateClientStatus(Long clientId, ClientStatus status);
    String deleteClient(Long clientId);
    ClientResponse getClientById(Long clientId);
    List<ClientResponse> getAllClients();
    ClientResponse updateClient(Long clientId, ClientRequest request);

}
