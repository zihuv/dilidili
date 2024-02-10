package com.zihuv.dilidili.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.common.contant.BusinessConstant;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.config.QiNiuConfig;
import com.zihuv.dilidili.exception.ServiceException;
import com.zihuv.dilidili.listener.event.RedisToDatabaseEvent;
import com.zihuv.dilidili.mapper.VideoMapper;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.model.param.VideoPublishParam;
import com.zihuv.dilidili.model.vo.HotVideoVO;
import com.zihuv.dilidili.service.VideoService;
import com.zihuv.dilidili.util.UserContext;
import com.zihuv.dilidili.util.cache.DistributedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.zihuv.dilidili.common.contant.RedisConstant.VIDEO_INFO;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private QiNiuConfig qiNiuConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DistributedCache distributedCache;

    @Override
    public String getUploadVideoToken() {
        return qiNiuConfig.videoUploadToken();
    }

    @Override
    public void publishVideo(VideoPublishParam videoPublishParam) {
        // TODO 需要添加视频标签；使用文件表的字段
        Video video = new Video();
        video.setVideoTitle(videoPublishParam.getVideoTitle());
        video.setVideoName(videoPublishParam.getVideoName());
        video.setVideoPath(qiNiuConfig.getDownloadUrl(videoPublishParam.getVideoName()));
        video.setPicPath(videoPublishParam.getPicPath());
        video.setUserId(UserContext.getUserId());
        video.setLikeAmount(0);
        video.setCollectAmount(0);
        video.setShareAmount(0);
        video.setIsReviewed(0);
        this.save(video);
    }

    @Override
    public void deleteVideo(Long videoId) {
        // TODO 只有本人的视频或者管理员才能删除视频
        this.removeById(videoId);
    }

    @Override
    public List<Video> getVideoByName(String videoTitle) {
        LambdaQueryWrapper<Video> lqw = new LambdaQueryWrapper<>();
        lqw.like(Video::getVideoTitle, videoTitle);
        return this.list(lqw);
    }

    @Override
    public List<HotVideoVO> getHotRankVideo() {
        Set<ZSetOperations.TypedTuple<Object>> idSet = redisTemplate.opsForZSet().reverseRangeWithScores(RedisConstant.HOT_RANK, 0, -1);
        if (CollUtil.isEmpty(idSet)) {
            throw new ServiceException("[热度排行榜] 热度视频排行榜为空");
        }
        List<HotVideoVO> hotVideoVOList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> objectTypedTuple : idSet) {
            Long videoId = Long.parseLong(String.valueOf(objectTypedTuple.getValue()));
            Double hotness = objectTypedTuple.getScore();
            HotVideoVO hotVideoVO = new HotVideoVO(videoId, hotness);
            hotVideoVO.hotFormat();
            hotVideoVOList.add(hotVideoVO);
        }
        return hotVideoVOList;
    }

    @Override
    public void incrementVideoViews(Long videoId) {
        // step1.在 redis 中更新播放量
        String key = RedisConstant.VIDEO_VIEWS + videoId;
        redisTemplate.opsForValue().increment(key, 1L);
        // step2.发布 redis 更新至数据库事件
        eventPublisher.publishEvent(new RedisToDatabaseEvent(key, BusinessConstant.VIDEO_VIEWS, 2));
    }

    @Override
    public Video getVideoById(Long videoId) {
        return distributedCache.get(VIDEO_INFO + videoId,
                Video.class,
                () -> this.getById(videoId));
    }

}




