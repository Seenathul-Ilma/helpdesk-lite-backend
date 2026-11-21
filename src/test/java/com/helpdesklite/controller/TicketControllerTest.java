package com.helpdesklite.controller;

import tools.jackson.databind.ObjectMapper;
import com.helpdesklite.enums.TicketPriority;
import com.helpdesklite.enums.TicketStatus;
import com.helpdesklite.dto.StatusUpdateRequest;
import com.helpdesklite.dto.TicketRequest;
import com.helpdesklite.dto.TicketResponse;
import com.helpdesklite.exception.InvalidStatusTransitionException;
import com.helpdesklite.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    @Test
    void createTicket_happyPath_returns201() throws Exception {
        TicketRequest request = new TicketRequest();
        request.setTitle("Broken monitor");
        request.setDescription("Screen flickering");
        request.setPriority(TicketPriority.HIGH);
        request.setRequesterName("Alice");
        request.setRequesterEmail("alice@example.com");

        TicketResponse response = new TicketResponse();
        response.setId(1L);
        response.setTitle("Broken monitor");
        response.setPriority(TicketPriority.HIGH);
        response.setStatus(TicketStatus.OPEN);
        response.setRequesterName("Alice");
        response.setRequesterEmail("alice@example.com");
        response.setCreatedAt(Instant.now());

        when(ticketService.createTicket(any(TicketRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Broken monitor"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        verify(ticketService).createTicket(any(TicketRequest.class));
    }

    @Test
    void createTicket_blankTitle_returns400() throws Exception {
        TicketRequest request = new TicketRequest();
        request.setTitle(""); // blank -> @NotBlank violation
        request.setPriority(TicketPriority.HIGH);
        request.setRequesterName("Alice");
        request.setRequesterEmail("alice@example.com");

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_invalidTransition_returns400() throws Exception {
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(TicketStatus.CLOSED); // OPEN -> CLOSED is invalid
        request.setActorRef("agent1");

        when(ticketService.updateStatus(eq(1L), eq(TicketStatus.CLOSED), anyString()))
                .thenThrow(new InvalidStatusTransitionException("Invalid transition from OPEN to CLOSED"));

        mockMvc.perform(patch("/api/tickets/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
