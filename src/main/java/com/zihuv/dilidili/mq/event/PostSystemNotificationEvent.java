package com.zihuv.dilidili.mq.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostSystemNotificationEvent {

    private String title;

    private String content;

    private LocalDateTime createTime;
}