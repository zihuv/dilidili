package com.zihuv.dilidili.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.Cache;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.FriendMapper;
import com.zihuv.dilidili.model.entity.Friend;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.vo.FriendVO;
import com.zihuv.dilidili.service.FriendService;
import com.zihuv.dilidili.service.UserService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zihuv.dilidili.common.contant.FriendConstant.OFFLINE;
import static com.zihuv.dilidili.common.contant.FriendConstant.ONLINE;
import static com.zihuv.dilidili.common.contant.RedisConstant.FRIEND_STATUS;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<Long, SseEmitter> sseEmitterCache;

    @Transactional(rollbackFor = Exception.class)
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

        // step3.添加好友，即把好友关系添加进数据库（好友双方都要添加至数据库，写扩散）
        Friend friend = new Friend();
        friend.setUserId(UserContext.getUserId());
        friend.setFriendId(friendId);
        this.save(friend);
        Friend friended = new Friend();
        friended.setUserId(friendId);
        friended.setFriendId(UserContext.getUserId());
        this.save(friended);
    }

    @Override
    public List<FriendVO> listFriend() {
        // step1.查询朋友列表关系
        LambdaQueryWrapper<Friend> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Friend::getUserId, UserContext.getUserId());
        List<Friend> friendList = this.list(lqw);
        if (CollUtil.isEmpty(friendList)) {
            return new ArrayList<>();
        }
        // step2.根据朋友的 id 查询他们的用户信息
        List<Long> ids = friendList.stream().map(Friend::getFriendId).toList();
        List<User> userList = userService.listByIds(ids);

        // step3.封装 VO 并返回
        List<FriendVO> friendVOList = new ArrayList<>();
        for (Friend friend : friendList) {
            String username = null;
            for (User user : userList) {
                if (user.getId().equals(friend.getFriendId())) {
                    username = user.getUsername();
                }
            }

            FriendVO friendVO = new FriendVO();
            friendVO.setFriendId(friend.getFriendId());
            friendVO.setFriendName(username);
            friendVO.setStatus(getFriendStatus(friend.getFriendId()));
            friendVOList.add(friendVO);
        }
        return friendVOList;
    }

    private Integer getFriendStatus(Long friendId) {
        // TODO 使用批量查找，减少网络 IO
        Object object = redisTemplate.opsForValue().get(FRIEND_STATUS + friendId);
        if (object == null) {
            return OFFLINE;
        }
        return ONLINE;
    }

    @Override
    public void heartbeat() {
        // 如果有段时间没上线，就要注册心跳，并通知好友我已上线信息
        if (redisTemplate.opsForValue().get(FRIEND_STATUS + UserContext.getUserId()) == null) {
            setHeartbeatKey();
            //  给在线的好友发送“我”上线的通知，即通过 sse 推送登录消息
            List<FriendVO> friendVOList = this.listFriend();
            for (FriendVO friendVO : friendVOList) {
                if (Objects.equals(friendVO.getStatus(), ONLINE)) {
                    sendBySse(StrUtil.format("[好友服务] 你的好友：{} 已经上线辣！", UserContext.getUserId()));
                }
            }
            return;
        }
        setHeartbeatKey();
        // 续约 SSE
        sendBySse("");
    }

    private void setHeartbeatKey() {
        redisTemplate.opsForValue().set(FRIEND_STATUS + UserContext.getUserId(), "", 2, TimeUnit.SECONDS);
    }

    private void sendBySse(String message) {
        try {
            SseEmitter sseEmitter = sseEmitterCache.getIfPresent(UserContext.getUserId());
            if (sseEmitter == null) {
                throw new ClientException("[通知服务] 通知推送 SSE 连接过期，请重新续约");
            }
            sseEmitter.send(message);
            sseEmitterCache.put(UserContext.getUserId(), sseEmitter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}