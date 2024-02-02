package com.zihuv.dilidili.mq.producer;

import com.zihuv.dilidili.mq.core.AbstractCommonSendProduceTemplate;
import com.zihuv.dilidili.mq.core.BaseMessage;
import com.zihuv.dilidili.mq.event.ReplyCommentEvent;
import org.springframework.stereotype.Component;

import static com.zihuv.dilidili.common.contant.MQConstant.RELAY_COMMENT_TAG_KEY;
import static com.zihuv.dilidili.common.contant.MQConstant.RELAY_COMMENT_TOPIC_KEY;

@Component
public class ReplyCommentProducer extends AbstractCommonSendProduceTemplate<ReplyCommentEvent> {

    @Override
    protected BaseMessage<ReplyCommentEvent> buildBaseMessage(ReplyCommentEvent messageSendEvent) {
        BaseMessage<ReplyCommentEvent> baseMessage = new BaseMessage<>();
        baseMessage.setEventName("回复消息通知");
        baseMessage.setTopic(RELAY_COMMENT_TOPIC_KEY);
        baseMessage.setTag(RELAY_COMMENT_TAG_KEY);
        baseMessage.setKeys(String.valueOf(System.currentTimeMillis()));
        baseMessage.setMessageBody(messageSendEvent);
        baseMessage.setSentTimeout(2000L);
        baseMessage.setDelayLevel(0);
        return baseMessage;
    }
}