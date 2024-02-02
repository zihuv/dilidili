package com.zihuv.dilidili.mq.event;

import lombok.Data;

@Data
public class ReplyCommentEvent {

    private Long userId;

    private Long replyUserId;

    private String originalComment;

    private String replyComment;
}