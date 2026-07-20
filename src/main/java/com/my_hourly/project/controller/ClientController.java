package com.my_hourly.project.controller;

import com.my_hourly.project.dto.ClientRequest;
import com.my_hourly.project.dto.ClientResponse;
import com.my_hourly.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    @Autowired
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest request) {
        return new ResponseEntity<>(clientService.createClient(request), HttpStatus.CREATED);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long clientId, @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(clientId, request));
    }

    @PatchMapping("/{clientId}")
    public ResponseEntity<ClientResponse> patchClient(@PathVariable Long clientId, @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.patchClient(clientId, request));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.ok("Client deleted successfully.");
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getClientById(clientId));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

}