package com.zihuv.dilidili.listener.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@ToString
public class RedisToDatabaseEvent extends ApplicationEvent implements Delayed {

    /**
     * 业务 id（0-点赞）
     */
    private Integer businessId;

    private Long id;

    private String redisKey;

    /**
     * 事件构造方法
     *
     * @param source       RedisKey
     * @param businessId   业务 id
     * @param delaySeconds 延迟时间
     */
    public RedisToDatabaseEvent(Object source, Integer businessId, long delaySeconds) {
        super(source, Clock.offset(Clock.systemDefaultZone(), Duration.ofSeconds(delaySeconds)));
        this.businessId = businessId;
    }

    @Override
    public int compareTo(Delayed o) {
        long delta = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return (int) delta;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long millis = this.getTimestamp();
        long currentTimeMillis = System.currentTimeMillis();
        long sourceDuration = millis - currentTimeMillis;
        return unit.convert(sourceDuration, unit);
    }

}