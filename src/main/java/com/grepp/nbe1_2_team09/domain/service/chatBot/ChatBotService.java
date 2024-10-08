package com.grepp.nbe1_2_team09.domain.service.chatBot;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.controller.chatBot.dto.ChatGPTReqDTO;
import com.grepp.nbe1_2_team09.controller.chatBot.dto.ChatGPTResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatBotService {

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiURL;
    @Value("${openai.api.key}")
    private String openAiKey;

    public String chat(String message) {
        String answer="오류가 발생했습니다. 다시 시도해주세요."; //이 멘트가 변경X = 오류 발생한 것
        try {
            ChatGPTReqDTO reqDTO = new ChatGPTReqDTO(model, message);

            RestTemplate restTemplate=new RestTemplate();
            restTemplate.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + openAiKey);
                return execution.execute(request, body);
            });

            ChatGPTResDTO chatGPTResDTO = restTemplate.postForObject(apiURL, reqDTO, ChatGPTResDTO.class);
            answer = chatGPTResDTO.getChoices().get(0).getMessage().getContent();
        }catch (Exception e){
            e.printStackTrace();
            log.warn(">>>> {} : {} <<<<", e, new AccountBookException(ExceptionMessage.FORMAT_ERROR));
            throw new AccountBookException(ExceptionMessage.FORMAT_ERROR);
        }
        return answer;
    }
}
