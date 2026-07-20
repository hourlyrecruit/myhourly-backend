package com.my_hourly.project.controller;

import com.my_hourly.project.dto.ClientRequest;
import com.my_hourly.project.dto.ClientResponse;
import com.my_hourly.project.entity.ClientStatus;
import com.my_hourly.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest request){
        return ResponseEntity.ok(clientService.createClient(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PatchMapping("/{clientId}/status")
    public ResponseEntity<ClientResponse> updateClientStatus(
            @PathVariable Long clientId,
            @RequestParam ClientStatus status) {

        return ResponseEntity.ok(
                clientService.updateClientStatus(clientId, status));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PatchMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long clientId, @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(clientId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.ok("Client deleted successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getClientById(clientId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

}