package com.helpdesklite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.helpdesklite.entity.Ticket;
import com.helpdesklite.enums.TicketPriority;
import com.helpdesklite.enums.TicketStatus;

@Repository 
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(TicketPriority priority);
    List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);
}
