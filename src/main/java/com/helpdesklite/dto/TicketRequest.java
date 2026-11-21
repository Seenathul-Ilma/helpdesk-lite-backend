package com.helpdesklite.dto;

import com.helpdesklite.enums.TicketPriority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketRequest {
    @NotBlank(message = "title is required")
    @Size(max = 200, message = "title must be at most 200 characters")
    private String title;

    @Size(max = 4000, message = "description must be at most 4000 characters")
    private String description;

    @NotNull(message = "priority is required")
    private TicketPriority priority;

    @NotBlank(message = "requester name is required")
    @Size(max = 100, message = "requester name must be at most 100 characters")
    private String requesterName;

    @NotBlank(message = "requester email is required")
    @Email(message = "requester email must be valid")
    private String requesterEmail;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
}
