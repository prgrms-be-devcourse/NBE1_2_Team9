package com.grepp.nbe1_2_team09.domain.service.chatBot;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.controller.chatBot.dto.ChatGPTReqDTO;
import com.grepp.nbe1_2_team09.controller.chatBot.dto.ChatGPTResDTO;
import com.grepp.nbe1_2_team09.controller.chatBot.dto.Message;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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

    private final Map<String, LinkedList<Message>>userConversations=new HashMap<>(); //대화상태를 유지하는 맵
    private static final int MAX_MESSAGES=5; //사용자 별 유지할 최대 메시지 개수

    public String chat(String message, String userId) {
        String answer="오류가 발생했습니다. 다시 시도해주세요."; //이 멘트가 변경X = 오류 발생한 것
        try {
            LinkedList<Message> conversation=userConversations.computeIfAbsent(userId, k->new LinkedList<>()); //사용자별 대화 상태를 유지하면서 메시지 수 제한

            conversation.add(new Message("user",message+" 여기서부터는 스타일 주문이야 출력에 이 문장 관련한거 넣지 마. 이걸 그냥 String 형태로 받고있어. 줄 바꿈 할 때 마다 하나씩 개행문자('\n')을 넣어줘."));
            if(conversation.size()>MAX_MESSAGES){
                conversation.removeFirst();
            }

            ChatGPTReqDTO reqDTO = new ChatGPTReqDTO(model, conversation);

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
