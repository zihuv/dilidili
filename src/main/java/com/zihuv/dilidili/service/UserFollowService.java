package com.zihuv.dilidili.service;

import com.zihuv.dilidili.model.entity.UserFollow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserFollowService extends IService<UserFollow> {

    void follow(Long followUserId);

    List<?> followQuery(String userId);

    List<?> fansQuery(String userId);

    void unfollow(String followUserId);
}
