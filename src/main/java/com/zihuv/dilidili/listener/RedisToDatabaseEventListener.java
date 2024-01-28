package com.zihuv.dilidili.listener;

import com.google.common.cache.Cache;
import com.zihuv.dilidili.exception.ServiceException;
import com.zihuv.dilidili.listener.event.RedisToDatabaseEvent;
import com.zihuv.dilidili.schedul.CounterQueueConsumer;
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
    private Cache<String, Boolean> eventLock;

    @EventListener
    @Async
    public void onApplicationEvent(RedisToDatabaseEvent event) {
        String redisKey = String.valueOf(event.getSource());
        // 如果存在 key，则说明事件已经被添加进队列，结束方法
        Boolean isPresent = eventLock.getIfPresent(redisKey);
        if (Boolean.TRUE.equals(isPresent)) {
            log.info("[事件] 为保证一定时间内事件的幂等性，已将事件：{} 丢弃", event);
            return;
        }
        eventLock.put(redisKey, true);

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