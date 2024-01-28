package com.zihuv.dilidili.service.like.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.listener.event.RedisToDatabaseEvent;
import com.zihuv.dilidili.mapper.like.LikeMapper;
import com.zihuv.dilidili.model.entity.Like;
import com.zihuv.dilidili.service.like.LikeService;
import com.zihuv.dilidili.service.video.VideoService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserContext userContext;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Override
    public void listLikeVideo() {

    }

    @Override
    public void likeVideo(Long videoId) {
        if (videoService.getById(videoId) == null) {
            throw new ClientException(StrUtil.format("[点赞视频] 视频 id：{} 不存在，不允许点赞该视频", videoId));
        }
        Long userId = userContext.getUserId();

        String key = RedisConstant.VIDEO_LIKE + videoId;
        LambdaQueryWrapper<Like> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Like::getLikeUserId, userId);
        lqw.eq(Like::getVideoId, videoId);

        if (CollUtil.isEmpty(this.list(lqw))) {
            // 用户如果没点赞过该视频，存存储进数据库添加计数器
            Like like = new Like();
            like.setLikeUserId(userId);
            like.setVideoId(videoId);
            this.save(like);
            // TODO 本地缓存批量点赞和 HyperLogLog
            redisTemplate.opsForValue().increment(key, 1);
            // 发布事件，将缓存更新至数据库
            eventPublisher.publishEvent(new RedisToDatabaseEvent(key,0,2));
        } else {
            // 用户之前点赞过视频，则取消点赞，删除数据库信息
            this.remove(lqw);
            redisTemplate.opsForValue().increment(key, -1);
        }
    }
}




