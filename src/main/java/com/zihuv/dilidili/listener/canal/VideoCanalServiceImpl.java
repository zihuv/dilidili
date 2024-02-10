package com.zihuv.dilidili.listener.canal;

import cn.hutool.core.bean.BeanUtil;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.listener.canal.core.ICanalService;
import com.zihuv.dilidili.model.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VideoCanalServiceImpl implements ICanalService<Video> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Video deserialize(Map<String, Object> map) {
        return BeanUtil.toBean(map, Video.class);
    }

    @Override
    public void insert(Map<String, Object> map) {
        Video video = deserialize(map);
        redisTemplate.opsForValue().set(RedisConstant.VIDEO_INFO + video.getId(), video);
    }

    @Override
    public void update(Map<String, Object> map) {
        Video video = deserialize(map);
        redisTemplate.opsForValue().set(RedisConstant.VIDEO_INFO + video.getId(), video);
    }

    @Override
    public void remove(Map<String, Object> map) {
        Video video = deserialize(map);
        redisTemplate.opsForValue().getAndDelete(RedisConstant.VIDEO_INFO + video.getId());
    }
}