package com.zihuv.dilidili.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.FriendMapper;
import com.zihuv.dilidili.model.entity.Friend;
import com.zihuv.dilidili.service.FriendService;
import com.zihuv.dilidili.service.UserService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    private UserService userService;

    @Override
    public void addFriend(Long friendId) {
        // step1.判断添加的用户是否存在
        if (userService.getById(friendId) == null) {
            throw new ClientException(StrUtil.format("[用户服务] 用户：{} 并不存在", friendId));
        }

        // step2.判断好友是否已经添加
        LambdaQueryWrapper<Friend> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Friend::getUserId, UserContext.getUserId());
        lqw.eq(Friend::getFriendId, friendId);
        if (this.getOne(lqw) != null) {
            throw new ClientException(StrUtil.format("[朋友服务] 用户：{} 已经是用户：{} 的好友", UserContext.getUserId(), friendId));
        }

        // step3.添加好友，即把好友关系添加进数据库
        Friend friend = new Friend();
        friend.setUserId(UserContext.getUserId());
        friend.setFriendId(friendId);
        this.save(friend);
    }

    @Override
    public List<?> listFriend() {
        LambdaQueryWrapper<Friend> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Friend::getUserId,UserContext.getUserId());
        return this.list(lqw);
    }
}