package com.zihuv.dilidili.mq.producer;

import com.zihuv.dilidili.mq.event.PostSystemNotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.zihuv.dilidili.common.contant.RedisConstant.SYSTEM_NOTIFICATION;

@Component
public class PostSystemNotificationProducer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 使用 redis list 数据结构实现类 MQ 拉消息功能
     * 原因：基于内存，速度更快；代码实现非常简单
     */
    public void sendMessage(PostSystemNotificationEvent event) {
        redisTemplate.opsForList().rightPush(SYSTEM_NOTIFICATION, event);
    }

}