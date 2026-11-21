package com.helpdesklite.controller;

import com.helpdesklite.enums.TicketPriority;
import com.helpdesklite.enums.TicketStatus;
import com.helpdesklite.dto.AssigneeRequest;
import com.helpdesklite.dto.HistoryEntryResponse;
import com.helpdesklite.dto.StatusUpdateRequest;
import com.helpdesklite.dto.TicketRequest;
import com.helpdesklite.dto.TicketResponse;
import com.helpdesklite.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
        TicketResponse created = ticketService.createTicket(request);
        return ResponseEntity.created(URI.create("/api/tickets/" + created.getId()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority) {
        return ResponseEntity.ok(ticketService.listTickets(status, priority));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        TicketResponse updated = ticketService.updateStatus(id, request.getStatus(), request.getActorRef());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/assignee")
    public ResponseEntity<TicketResponse> assign(
            @PathVariable Long id,
            @RequestBody AssigneeRequest request) {
        TicketResponse updated = ticketService.assignAssignee(id, request.getAssigneeRef(), null);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HistoryEntryResponse>> history(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getHistory(id));
    }
}