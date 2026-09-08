package com.ruflo.ticketing.api;

import com.ruflo.ticketing.domain.Ticket;
import com.ruflo.ticketing.domain.TicketStatus;
import com.ruflo.ticketing.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@Validated
@Tag(name = "Tickets", description = "Ticket CRUD and search endpoints")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "List and search tickets")
    public List<Ticket> listTickets(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        return ticketService.list(query, status, sortBy, sortOrder);
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get ticket by ID")
    public Ticket getTicket(@PathVariable long ticketId) {
        return ticketService.getById(ticketId);
    }

    @PostMapping
    @Operation(summary = "Create ticket")
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        Ticket created = ticketService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{ticketId}")
    @Operation(summary = "Update ticket")
    public Ticket updateTicket(@PathVariable long ticketId, @Valid @RequestBody TicketUpdateRequest request) {
        return ticketService.update(ticketId, request);
    }

    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Delete ticket")
    public ResponseEntity<Void> deleteTicket(@PathVariable long ticketId) {
        ticketService.delete(ticketId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @Operation(summary = "Get ticket summary")
    public TicketSummaryResponse ticketSummary() {
        return new TicketSummaryResponse(ticketService.count(), ticketService.summary());
    }
}
