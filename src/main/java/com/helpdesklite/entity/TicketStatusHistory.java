package com.helpdesklite.entity;

import com.helpdesklite.enums.TicketStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ticket_status_history", indexes = {
    @Index(name = "idx_history_ticket_id", columnList = "ticket_id")
})
public class TicketStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private TicketStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 20, nullable = false)
    private TicketStatus newStatus;

    @Column(name = "actor_ref", length = 255)
    private String actorRef;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public TicketStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(TicketStatus oldStatus) { this.oldStatus = oldStatus; }
    public TicketStatus getNewStatus() { return newStatus; }
    public void setNewStatus(TicketStatus newStatus) { this.newStatus = newStatus; }
    public String getActorRef() { return actorRef; }
    public void setActorRef(String actorRef) { this.actorRef = actorRef; }
    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
}