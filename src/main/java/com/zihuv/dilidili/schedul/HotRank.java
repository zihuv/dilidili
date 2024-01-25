package com.zihuv.dilidili.schedul;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.exception.ServiceException;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.model.vo.HotVideoVO;
import com.zihuv.dilidili.service.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class HotRank {

    // 热度权重
    private static final double PLAY_COUNT_WEIGHT = 0.2;
    private static final double LIKE_COUNT_WEIGHT = 0.2;
    private static final double COMMENT_COUNT_WEIGHT = 0.3;
    private static final double SHARE_COUNT_WEIGHT = 0.3;
    // 热度的时间衰减因子
    private static final double TIME_DECAY_FACTOR = 0.05;

    // 每次查询视频的数量
    private static final int LIMIT = 1000;
    // 排行榜显示的视频数量
    private static final int HOT_VIDEO_SIZE = 10;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

    // @Scheduled(cron = "* * * * * *")
    @Scheduled(cron = "0 0 */1 * * ?")
    public void hotRank() {
        final TopVideoQueue topVideoQueue = new TopVideoQueue(
                HOT_VIDEO_SIZE,
                new PriorityQueue<>(HOT_VIDEO_SIZE, Comparator.comparing(HotVideoVO::getHotness)));
        // 查询并计算七天内的所有视频的热度
        // TODO 暂时使用 CreateTime 代表视频的发布时间
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long tempId = 0;
        List<Video> videoList;
        while (true) {
            LambdaQueryWrapper<Video> lqw = new LambdaQueryWrapper<>();
            lqw.select(Video::getId, Video::getPlayAmount, Video::getLikeAmount, Video::getCommentAmount,
                    Video::getShareAmount, Video::getCreateTime);
            lqw.gt(Video::getId, tempId);
            lqw.gt(Video::getCreateTime, sevenDaysAgo);
            lqw.last("limit " + LIMIT);
            videoList = videoService.list(lqw);
            // 如果将所有数据查询完毕，就跳出循环
            if (CollUtil.isEmpty(videoList)) {
                break;
            }

            for (Video video : videoList) {
                Double hotness = getVideoHotness(video);
                HotVideoVO hotVideoVO = new HotVideoVO(video.getId(), hotness);
                topVideoQueue.add(hotVideoVO);
            }
            // 获取分页查询中最后一个视频的 id，使用该 id 继续往下查询视频信息
            tempId = videoList.get(videoList.size() - 1).getId();
        }

        // 使用 Pipelined 将排行榜数据发送给 redis
        byte[] key = RedisConstant.HOT_RANK.getBytes();
        List<HotVideoVO> hotVideoVOList = topVideoQueue.get();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (HotVideoVO hotVideoVO : hotVideoVOList) {
                Double hotness = hotVideoVO.getHotness();
                byte[] hotVideoBytes = jackson2JsonRedisSerializer.serialize(hotVideoVO.getVideoId());
                if (ObjectUtils.isEmpty(hotVideoBytes)) {
                    throw new ServiceException(StrUtil.format("[热度排行榜] 视频：{} 序列化为 bytes 为 null", hotVideoVO));
                }
                connection.zAdd(key, hotness, hotVideoBytes);
            }
            return null;
        });
    }

    // 计算热度
    public Double getVideoHotness(Video video) {
        // TODO 暂时使用 CreateTime 代表视频的发布时间
        // 计算时间衰减因子
        double timeDecay = Math.exp(-TIME_DECAY_FACTOR * ChronoUnit.HOURS.between(video.getCreateTime(), LocalDateTime.now()));
        // 计算热度
        return (PLAY_COUNT_WEIGHT * video.getPlayAmount() +
                LIKE_COUNT_WEIGHT * video.getLikeAmount() +
                COMMENT_COUNT_WEIGHT * video.getCommentAmount() +
                SHARE_COUNT_WEIGHT * video.getShareAmount())
                * timeDecay;
    }
}