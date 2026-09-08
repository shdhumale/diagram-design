package com.ruflo.ticketing.service;

import com.ruflo.ticketing.api.TicketCreateRequest;
import com.ruflo.ticketing.api.TicketUpdateRequest;
import com.ruflo.ticketing.domain.Ticket;
import com.ruflo.ticketing.domain.TicketStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketService {

    private static final Map<TicketStatus, List<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            TicketStatus.New, List.of(TicketStatus.Assigned, TicketStatus.Done, TicketStatus.Escalate),
            TicketStatus.Assigned, List.of(TicketStatus.New, TicketStatus.Done, TicketStatus.Escalate),
            TicketStatus.Escalate, List.of(TicketStatus.Assigned, TicketStatus.Done),
            TicketStatus.Done, List.of(TicketStatus.Assigned)
    );

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, Ticket> tickets = new ConcurrentHashMap<>();

    public Ticket create(TicketCreateRequest request) {
        long id = idSequence.incrementAndGet();
        Instant now = Instant.now();

        Ticket ticket = new Ticket(
                id,
                request.getName().trim(),
                request.getDescription(),
                request.getStatus(),
                now,
                now
        );
        tickets.put(id, ticket);
        return ticket;
    }

    public Ticket getById(long id) {
        Ticket ticket = tickets.get(id);
        if (ticket == null) {
            throw new NoSuchElementException("Ticket not found: " + id);
        }
        return ticket;
    }

    public List<Ticket> list(String query, TicketStatus status, String sortBy, String sortOrder) {
        List<Ticket> result = new ArrayList<>(tickets.values());

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase(Locale.ROOT);
            result = result.stream()
                    .filter(t -> t.getName().toLowerCase(Locale.ROOT).contains(q)
                            || (t.getDescription() != null && t.getDescription().toLowerCase(Locale.ROOT).contains(q)))
                    .toList();
        }

        if (status != null) {
            result = result.stream().filter(t -> t.getStatus() == status).toList();
        }

        Comparator<Ticket> comparator = switch (sortBy == null ? "id" : sortBy) {
            case "name" -> Comparator.comparing(Ticket::getName, String.CASE_INSENSITIVE_ORDER);
            case "status" -> Comparator.comparing(t -> t.getStatus().name());
            case "createdAt" -> Comparator.comparing(Ticket::getCreatedAt);
            case "updatedAt" -> Comparator.comparing(Ticket::getUpdatedAt);
            default -> Comparator.comparingLong(Ticket::getId);
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return result.stream().sorted(comparator).toList();
    }

    public Ticket update(long id, TicketUpdateRequest request) {
        Ticket current = getById(id);

        if (current.getStatus() != request.getStatus()) {
            List<TicketStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current.getStatus(), List.of());
            if (!allowed.contains(request.getStatus())) {
                throw new IllegalStateException("Invalid status transition from " + current.getStatus() + " to " + request.getStatus());
            }
        }

        current.setName(request.getName().trim());
        current.setDescription(request.getDescription());
        current.setStatus(request.getStatus());
        current.setUpdatedAt(Instant.now());
        tickets.put(id, current);

        return current;
    }

    public void delete(long id) {
        if (tickets.remove(id) == null) {
            throw new NoSuchElementException("Ticket not found: " + id);
        }
    }

    public Map<String, Long> summary() {
        Map<TicketStatus, Long> counts = new EnumMap<>(TicketStatus.class);
        for (TicketStatus status : TicketStatus.values()) {
            counts.put(status, 0L);
        }
        tickets.values().forEach(t -> counts.computeIfPresent(t.getStatus(), (k, v) -> v + 1));

        Map<String, Long> result = new ConcurrentHashMap<>();
        counts.forEach((k, v) -> result.put(k.name(), v));
        return result;
    }

    public long count() {
        return tickets.size();
    }
}
