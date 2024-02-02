package com.zihuv.dilidili.mq.comsumer;

import com.zihuv.dilidili.model.entity.Notification;
import com.zihuv.dilidili.mq.core.MessageWrapper;
import com.zihuv.dilidili.mq.event.ReplyCommentEvent;
import com.zihuv.dilidili.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.zihuv.dilidili.common.contant.MQConstant.*;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = RELAY_COMMENT_TOPIC_KEY,
        selectorExpression = RELAY_COMMENT_TAG_KEY,
        consumerGroup = RELAY_COMMENT_CG_KEY
)
public class ReplyCommentConsumer implements RocketMQListener<MessageWrapper<ReplyCommentEvent>> {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void onMessage(MessageWrapper<ReplyCommentEvent> message) {
        // step1.转换数据类型
        ReplyCommentEvent event = message.getMessage();
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setReplyUserId(event.getReplyUserId());
        notification.setOriginalComment(event.getOriginalComment());
        notification.setReplyComment(  event.getReplyComment());
        notification.setIsRead(0);

        // step2.存储至通知数据库
        notificationService.save(notification);
    }
}