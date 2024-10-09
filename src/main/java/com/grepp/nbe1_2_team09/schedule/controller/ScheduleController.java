package com.grepp.nbe1_2_team09.schedule.controller;

import com.grepp.nbe1_2_team09.schedule.controller.dto.SelectionData;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ScheduleController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/selectCell") // 클라이언트에서 /app/selectCell로 전송한 메시지 처리
    public void handleCellSelection(SelectionData selectionData) {
        System.out.println("수신한 선택 데이터: " + selectionData); // 수신 데이터 로그
        // 선택된 셀 정보를 다른 모든 클라이언트에 브로드캐스트
        messagingTemplate.convertAndSend("/topic/selectedCells", selectionData);
    }

}
