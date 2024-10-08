package com.grepp.nbe1_2_team09.controller.chatBot.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ChatGPTReqDTO {
    private String model;
    private List<Message> messages;
    private Integer max_tokens;

    public ChatGPTReqDTO(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = 1000;
    }
}

