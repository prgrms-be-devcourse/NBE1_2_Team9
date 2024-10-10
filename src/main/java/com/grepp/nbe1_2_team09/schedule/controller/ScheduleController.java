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

        messagingTemplate.convertAndSend("/topic/selectedCells", selectionData);
    }

    @MessageMapping("/deleteCell") // 클라이언트에서 /app/selectCell로 전송한 메시지 처리
    public void handleCellDeleteCell(SelectionData selectionData) {
        messagingTemplate.convertAndSend("/topic/deletedCells", selectionData);
    }

}
