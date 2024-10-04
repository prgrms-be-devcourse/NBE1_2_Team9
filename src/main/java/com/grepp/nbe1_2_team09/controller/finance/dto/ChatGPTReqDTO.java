package com.grepp.nbe1_2_team09.controller.finance.dto;

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
        this.messages.add(new Message("user", prompt +"Please convert this word into 'yyyy-MM-ddTHH:mm:ss' format. If you don't have enough time information, you can infer and fill it in. Instead, you have to keep this format 'yyyy-MM-ddTHH:mm:ss', and when you print it out, just 'yyyy-MM-ddTHH:mm:ss'. Don't print anything else"));
    }
}

