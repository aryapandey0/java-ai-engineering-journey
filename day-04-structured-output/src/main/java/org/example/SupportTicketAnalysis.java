package org.example;

public record SupportTicketAnalysis(
        TicketCategory category,
        TicketPriority priority,
        Sentiment sentiment,
        String summary
) {
}