package com.zihuv.dilidili.schedul;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.cache.Cache;
import com.zihuv.dilidili.listener.event.RedisToDatabaseEvent;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.service.video.VideoService;
import com.zihuv.dilidili.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

@Slf4j
@Component
public class CounterQueueConsumer {

    public static DelayQueue<RedisToDatabaseEvent> queue = new DelayQueue<>();

    @Autowired
    private VideoService videoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Boolean> eventLock;

    // 每秒都去消费队列
    @Scheduled(cron = "* * * * * *")
    public void redisToDatabase() {
        while (true) {
            RedisToDatabaseEvent event = queue.poll();
            if (event == null) {
                break;
            }

            Long count = JSON.toBean(redisTemplate.opsForValue().get(event.getRedisKey()), Long.class);
            LambdaUpdateWrapper<Video> luw = new LambdaUpdateWrapper<>();
            luw.eq(Video::getId, event.getId());
            luw.set(Video::getLikeAmount, count);
            videoService.update(luw);
            // 事件处理完毕，将锁删除
            eventLock.invalidate(event.getRedisKey());
            log.info("[消费队列] 事件：{} 消费成功", event);
        }
    }

}