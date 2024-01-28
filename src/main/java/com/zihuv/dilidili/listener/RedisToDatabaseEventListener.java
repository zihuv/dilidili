package com.zihuv.dilidili.listener;

import com.zihuv.dilidili.exception.ServiceException;
import com.zihuv.dilidili.listener.event.RedisToDatabaseEvent;
import com.zihuv.dilidili.schedul.CounterQueueConsumer;
import com.zihuv.dilidili.service.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisToDatabaseEventListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private VideoService videoService;

    @EventListener
    @Async
    public void onApplicationEvent(RedisToDatabaseEvent event) {
        String redisKey = String.valueOf(event.getSource());
        String[] split = redisKey.split(":");
        Long id = Long.parseLong(split[split.length - 1]);

        event.setRedisKey(redisKey);
        event.setId(id);
        boolean add = CounterQueueConsumer.queue.offer(event);
        if (!add) {
            throw new ServiceException("[事件] 添加元素进入队列失败");
        }
        log.info("[事件] 事件：{} 添加进入延时队列成功", event);
    }
}