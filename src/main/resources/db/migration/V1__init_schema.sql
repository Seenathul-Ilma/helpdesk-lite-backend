CREATE TABLE tickets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(4000),
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    requester_name VARCHAR(100) NOT NULL,
    requester_email VARCHAR(255),
    assignee_ref VARCHAR(255),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    resolved_at DATETIME(6),
    PRIMARY KEY (id),
    INDEX idx_tickets_status (status),
    INDEX idx_tickets_priority (priority),
    INDEX idx_tickets_requester_email (requester_email),
    INDEX idx_tickets_assignee_ref (assignee_ref)
) ENGINE=InnoDB;

CREATE TABLE ticket_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    actor_ref VARCHAR(255),
    changed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_history_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    INDEX idx_history_ticket_id (ticket_id)
) ENGINE=InnoDB;
