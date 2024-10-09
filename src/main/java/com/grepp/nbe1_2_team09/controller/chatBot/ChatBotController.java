package com.grepp.nbe1_2_team09.controller.chatBot;

import com.grepp.nbe1_2_team09.admin.service.CustomUserDetails;
import com.grepp.nbe1_2_team09.domain.service.chatBot.ChatBotService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping
    public String chat(@RequestBody Map<String, String> message, @AuthenticationPrincipal CustomUserDetails user) {
        String userId=user.getUsername();
        return chatBotService.chat(message.get("message"), userId);
    }
}
