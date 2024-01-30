package com.zihuv.dilidili.service;

import com.zihuv.dilidili.model.entity.Like;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface LikeService extends IService<Like> {

    List<?> listLikeVideo();

    void likeVideo(Long videoId);
}
