package example;

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

    public String generateResponse(String prompt) throws IOException, InterruptedException {
        Map request = Map.of(
                "model","llama3.2:3b",
                "messages",List.of(
                        Map.of("role","system",
                                "content","You are a helpful java mentor"),
                        Map.of("role","user",
                                "content",prompt)
        ),
                "stream",false,
                "options",Map.of("temperature",0.7)
        );

        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type","application/json")
                .timeout(Duration.ofMinutes(3))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

     HttpResponse<String> response = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());

     if(response.statusCode() < 200 || response.statusCode() >= 300){
         throw new IOException(
                 "Ollama API error: " +
                         response.statusCode() +
                         " " + response.body()
         );
     }

     JsonNode root = objectMapper.readTree(response.body());
     JsonNode content = root.path("message").path("content");

        if (!content.isTextual()) {
            throw new IOException(
                    "Invalid response from Ollama"
            );
        }

        return content.asText();

    }

}
