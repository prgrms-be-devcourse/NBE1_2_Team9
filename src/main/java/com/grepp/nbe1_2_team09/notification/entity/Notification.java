package com.grepp.nbe1_2_team09.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {
    @Id
    private String id;
    private String type;
    private String message;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private boolean read;
    private Long invitationId;

    public void setRead(boolean read) {
        this.read = read;
    }
}
