package com.zihuv.dilidili.service.like;

import com.zihuv.dilidili.model.entity.Like;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LikeService extends IService<Like> {

    void listLikeVideo();

    void likeVideo(Long videoId);
}
