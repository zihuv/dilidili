package com.zihuv.dilidili.service;

import com.zihuv.dilidili.model.entity.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FriendService extends IService<Friend> {

    void addFriend(Long friendId);

    List<?> listFriend();
}