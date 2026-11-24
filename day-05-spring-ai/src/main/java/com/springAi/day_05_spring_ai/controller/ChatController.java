package com.springAi.day_05_spring_ai.controller;

import com.springAi.day_05_spring_ai.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service){
        this.service=service;
    }

    @PostMapping
    public Map<String,String> getResponse(@RequestBody Map<String,String> request){
        String message = request.get("message");

        String response = service.chat(message);

        return Map.of("response", response);
    }



}
