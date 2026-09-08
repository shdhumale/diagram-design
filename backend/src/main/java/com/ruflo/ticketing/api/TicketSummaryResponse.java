package com.ruflo.ticketing.api;

import java.util.Map;

public record TicketSummaryResponse(long totalTickets, Map<String, Long> byStatus) {
}
