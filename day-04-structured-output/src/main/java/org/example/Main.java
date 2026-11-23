package org.example;

public class Main {

    public static void main(String[] args) {

        LlmClient client = new LlmClient();

        String ticket = """
                My payment was deducted but my order
                was not placed. Please resolve this urgently.
                """;

        try {

            SupportTicketAnalysis analysis =
                    client.getAnalysis(ticket);

            System.out.println("TICKET ANALYSIS");
            System.out.println("--------------------");

            System.out.println(
                    "Category: " + analysis.category()
            );

            System.out.println(
                    "Priority: " + analysis.priority()
            );

            System.out.println(
                    "Sentiment: " + analysis.sentiment()
            );

            System.out.println(
                    "Summary: " + analysis.summary()
            );

        } catch (Exception e) {

            System.err.println(
                    "Ticket analysis failed: " + e.getMessage()
            );
        }
    }
}