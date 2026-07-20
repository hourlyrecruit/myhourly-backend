package com.my_hourly.project.service;

import com.my_hourly.project.dto.ClientRequest;
import com.my_hourly.project.dto.ClientResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ClientService {
    ClientResponse createClient(ClientRequest request);
    ClientResponse updateClient(Long clientId, ClientRequest request);
    String deleteClient(Long clientId);
    ClientResponse getClientById(Long clientId);
    List<ClientResponse> getAllClients();
    ClientResponse patchClient(Long clientId, ClientRequest request);

}
