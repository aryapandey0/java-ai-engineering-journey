package example;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {

        LlmClient client = new LlmClient();

        String response = client.generateResponse(
                "Explain Java multithreading with an example."
        );

        System.out.println("AI RESPONSE:");
        System.out.println(response);


    }
}