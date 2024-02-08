package com.zihuv.dilidili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.Cache;
import com.zihuv.dilidili.mapper.NotificationMapper;
import com.zihuv.dilidili.model.entity.Notification;
import com.zihuv.dilidili.model.param.PostSystemNotificationParam;
import com.zihuv.dilidili.mq.event.PostSystemNotificationEvent;
import com.zihuv.dilidili.service.NotificationService;
import com.zihuv.dilidili.util.JSON;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.zihuv.dilidili.common.contant.RedisConstant.SYSTEM_NOTIFICATION;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<Long, SseEmitter> sseEmitterCache;

    @Override
    public List<?> listReplyNotification() {
        LambdaQueryWrapper<Notification> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Notification::getReplyUserId, UserContext.getUserId());
        return this.list(lqw);
    }

    @Override
    public List<?> listSystemNotification() {
        // step1.拉取消息
        // TODO 设置已读后的偏移量
        long offset = 0;
        List<Object> objectList = redisTemplate.opsForList().range(SYSTEM_NOTIFICATION, offset, -1);
        // step2.转换消息数据类型，并返回
        return JSON.toList(objectList, PostSystemNotificationEvent.class);
    }

    @Override
    public void postSystemNotification(PostSystemNotificationParam requestParam) {
        PostSystemNotificationEvent event = new PostSystemNotificationEvent();
        event.setTitle(requestParam.getTitle());
        event.setContent(requestParam.getContent());
        event.setCreateTime(LocalDateTime.now());
        redisTemplate.opsForList().rightPush(SYSTEM_NOTIFICATION, event);
    }

    @Override
    public SseEmitter pushNotification() {
        // 销毁 SseEmitter 时机：
        // 1. 心跳服务无法查询到该用户，即用户已下线
        // 2. sse 服务自己过期了

        // 续期时机：心跳确认用户在线，续约 sse
        // sse 续约办法：发送一个空字符串，并在缓存中更新
        SseEmitter sseEmitter = new SseEmitter(300L);
        try {
            sseEmitter.send("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sseEmitterCache.put(UserContext.getUserId(), sseEmitter);
        return sseEmitter;
    }
}