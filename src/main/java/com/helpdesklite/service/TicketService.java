package com.helpdesklite.service;

import com.helpdesklite.entity.Ticket;
import com.helpdesklite.entity.TicketStatusHistory;
import com.helpdesklite.enums.TicketPriority;
import com.helpdesklite.enums.TicketStatus;
import com.helpdesklite.dto.HistoryEntryResponse;
import com.helpdesklite.dto.TicketRequest;
import com.helpdesklite.dto.TicketResponse;
import com.helpdesklite.exception.InvalidStatusTransitionException;
import com.helpdesklite.exception.TicketNotFoundException;
import com.helpdesklite.repository.TicketRepository;
import com.helpdesklite.repository.TicketStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository historyRepository;

    public List<TicketResponse> listTickets(TicketStatus status, TicketPriority priority) {
        List<Ticket> tickets;
        if (status != null && priority != null) {
            tickets = ticketRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else if (priority != null) {
            tickets = ticketRepository.findByPriority(priority);
        } else {
            tickets = ticketRepository.findAll();
        }
        return tickets.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse getTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setRequesterName(request.getRequesterName());
        ticket.setRequesterEmail(request.getRequesterEmail());
        Ticket saved = ticketRepository.save(ticket);
        return toResponse(saved);
    }

    @Transactional
    public TicketResponse updateStatus(Long id, TicketStatus newStatus, String actorRef) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        TicketStatus oldStatus = ticket.getStatus();
        validateTransition(oldStatus, newStatus);

        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.RESOLVED || newStatus == TicketStatus.CLOSED) {
            ticket.setResolvedAt(Instant.now());
        }

        Ticket saved = ticketRepository.save(ticket);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(saved.getId());
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setActorRef(actorRef);
        history.setChangedAt(Instant.now());
        historyRepository.save(history);

        return toResponse(saved);
    }

    @Transactional
    public TicketResponse assignAssignee(Long id, String assigneeRef, String actorRef) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticket.setAssigneeRef(assigneeRef);
        Ticket saved = ticketRepository.save(ticket);
        return toResponse(saved);
    }

    public List<HistoryEntryResponse> getHistory(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(ticketId);
        }
        return historyRepository.findByTicketIdOrderByChangedAtAsc(ticketId).stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());
    }

    private void validateTransition(TicketStatus oldStatus, TicketStatus newStatus) {
        if (oldStatus == newStatus) {
            throw new InvalidStatusTransitionException("Ticket is already " + newStatus);
        }
        boolean valid = switch (oldStatus) {
            case OPEN -> newStatus == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> newStatus == TicketStatus.RESOLVED;
            case RESOLVED -> newStatus == TicketStatus.CLOSED;
            case CLOSED -> false;
        };
        if (!valid) {
            throw new InvalidStatusTransitionException(
                    "Invalid transition from " + oldStatus + " to " + newStatus);
        }
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse r = new TicketResponse();
        r.setId(ticket.getId());
        r.setTitle(ticket.getTitle());
        r.setDescription(ticket.getDescription());
        r.setPriority(ticket.getPriority());
        r.setStatus(ticket.getStatus());
        r.setRequesterName(ticket.getRequesterName());
        r.setRequesterEmail(ticket.getRequesterEmail());
        r.setAssigneeRef(ticket.getAssigneeRef());
        r.setCreatedAt(ticket.getCreatedAt());
        r.setUpdatedAt(ticket.getUpdatedAt());
        r.setResolvedAt(ticket.getResolvedAt());
        return r;
    }

    private HistoryEntryResponse toHistoryResponse(TicketStatusHistory h) {
        HistoryEntryResponse r = new HistoryEntryResponse();
        r.setId(h.getId());
        r.setOldStatus(h.getOldStatus());
        r.setNewStatus(h.getNewStatus());
        r.setActorRef(h.getActorRef());
        r.setChangedAt(h.getChangedAt());
        return r;
    }
}
