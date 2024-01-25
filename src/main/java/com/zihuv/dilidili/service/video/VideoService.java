package com.zihuv.dilidili.service.video;

import com.zihuv.dilidili.model.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.dilidili.model.param.VideoPublishParam;
import com.zihuv.dilidili.model.vo.HotVideoVO;

import java.util.List;

public interface VideoService extends IService<Video> {

    String getUploadVideoToken();

    /**
     * 发布视频，并将视频信息存储至数据库
     */
    void publishVideo(VideoPublishParam videoPublishParam);

    void deleteVideo(Long videoId);

    List<Video> getVideoByName(String videoTitle);

    List<HotVideoVO> getHotRankVideo();
}
