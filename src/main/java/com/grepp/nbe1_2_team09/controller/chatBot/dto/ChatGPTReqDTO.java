package com.grepp.nbe1_2_team09.controller.chatBot.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ChatGPTReqDTO {
    private String model;
    private List<Message> messages;

    public ChatGPTReqDTO(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }
}

