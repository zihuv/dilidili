package com.zihuv.dilidili.service.video.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.config.QiNiuConfig;
import com.zihuv.dilidili.mapper.video.VideoMapper;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.model.param.VideoPublishParam;
import com.zihuv.dilidili.service.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private QiNiuConfig qiNiuConfig;

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


}




