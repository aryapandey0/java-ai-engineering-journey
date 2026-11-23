package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LlmClient {
    private final HttpClient httpClient;
    private static final String API_URL = "http://localhost:11434/api/chat";
    private final ObjectMapper objectMapper;
    public LlmClient(){
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        this.objectMapper = new ObjectMapper();
    }


    public SupportTicketAnalysis getAnalysis(String ticket) throws IOException, InterruptedException {
        String systemPropmt = """
                You are a customer support ticket classifier.

                Analyze the customer's complaint.

                Rules:
                1. Select the most appropriate category.
                2. Assign an appropriate priority.
                3. Identify customer sentiment.
                4. Write a concise summary.
                5. Do not invent information.
                6. Return only data matching the JSON schema.
                """;
        Map request = Map.of(
                "model","llama3.2:3b",
                "messages",List.of(
                        Map.of("role","system",
                                "content",systemPropmt),
                Map.of("role","user",
                        "content",ticket)
        ),
                "stream", false,

                "format", getTicketSchema(),

                "options", Map.of(
                        "temperature", 0
                )
        );

        String json = objectMapper.writeValueAsString(request);
        HttpRequest requestTo = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(3))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Send request

        HttpResponse<String> response =
                httpClient.send(
                        requestTo,
                        HttpResponse.BodyHandlers.ofString()
                );

        // Handle HTTP errors

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new IOException(
                    "Ollama error: " +
                            response.statusCode() +
                            " " + response.body()
            );
        }

        // Parse outer Ollama response

        JsonNode root =
                objectMapper.readTree(response.body());

        JsonNode content =
                root.path("message").path("content");

        if (!content.isTextual() ||
                content.asText().isBlank()) {

            throw new IOException(
                    "Empty or invalid LLM response"
            );
        }

        // Deserialize generated JSON into Java DTO

        SupportTicketAnalysis analysis =
                objectMapper.readValue(
                        content.asText(),
                        SupportTicketAnalysis.class
                );

        // Validate DTO

        TicketValidator.validate(analysis);

        return analysis;
    }

    private JsonNode getTicketSchema() throws IOException {

        String schema = """
        {
          "type": "object",
          "properties": {
            "category": {
              "type": "string",
              "enum": [
                "PAYMENT",
                "TECHNICAL",
                "ACCOUNT",
                "OTHER"
              ]
            },
            "priority": {
              "type": "string",
              "enum": [
                "LOW",
                "MEDIUM",
                "HIGH"
              ]
            },
            "sentiment": {
              "type": "string",
              "enum": [
                "POSITIVE",
                "NEUTRAL",
                "NEGATIVE"
              ]
            },
            "summary": {
              "type": "string"
            }
          },
          "required": [
            "category",
            "priority",
            "sentiment",
            "summary"
          ],
          "additionalProperties": false
        }
        """;

        return objectMapper.readTree(schema);
    }


}
