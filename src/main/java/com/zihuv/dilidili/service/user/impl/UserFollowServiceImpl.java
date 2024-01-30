package com.zihuv.dilidili.service.user.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.exception.ServiceException;
import com.zihuv.dilidili.mapper.user.UserFollowMapper;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.entity.UserFollow;
import com.zihuv.dilidili.model.vo.FollowVO;
import com.zihuv.dilidili.service.user.UserFollowService;
import com.zihuv.dilidili.service.user.UserService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void follow(Long followUserId) {
        Long userId = UserContext.getUserId();
        if (userId.equals(followUserId)) {
            throw new ClientException("[关注模块] 不可以关注你自己");
        }
        // 查询 subscribeId 的用户是否真实存在
        User user = userService.getById(followUserId);
        if (user == null) {
            throw new ClientException(StrUtil.format("[关注模块] 用户 id：{} 并不存在", followUserId));
        }
        // 校验是否重复关注
        LambdaQueryWrapper<UserFollow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFollow::getUserId, userId);
        lqw.eq(UserFollow::getFollowUserId, followUserId);
        List<UserFollow> userFollowList = this.list(lqw);
        if (CollUtil.isNotEmpty(userFollowList)) {
            throw new ClientException(StrUtil.format("[关注模块] 用户：{} 已经关注用户：{}", userId, followUserId));
        }

        try {
            UserFollow userFollow = new UserFollow();
            userFollow.setUserId(userId);
            userFollow.setFollowUserId(followUserId);
            this.save(userFollow);
            LocalTime localTime = LocalTime.now();
            // TODO 目前缓存没有实际用处，在查询中依然是直接查数据库
            // 添加到自己的关注列表
            redisTemplate.opsForZSet().add(RedisConstant.USER_FOLLOW + userId, followUserId, localTime.getNano());
            // 添加到对方粉丝列表
            redisTemplate.opsForZSet().add(RedisConstant.USER_FANS + followUserId, userId, localTime.getNano());
        } catch (Exception e) {
            // 出异常的时候删除 key
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FOLLOW + userId, followUserId);
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FANS + followUserId, userId);
            // 抛出异常，让数据库回滚
            throw new ServiceException(StrUtil.format("[关注模块] 用户：{} 关注用户：{} 出现异常", userId, followUserId));
        }
    }

    @Override
    public List<?> followQuery(String userId) {
        // 根据用户 id 查询订阅者 id
        LambdaQueryWrapper<UserFollow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFollow::getUserId, userId);
        List<UserFollow> userFollowList = this.list(lqw);

        if (CollUtil.isEmpty(userFollowList)) {
            return new ArrayList<>();
        }

        // 将订阅者 id 放在 set 集合中
        Set<Long> followUserIdSet = new HashSet<>();
        for (UserFollow userFollow : userFollowList) {
            followUserIdSet.add(userFollow.getFollowUserId());
        }
        // 根据订阅者 id 查询这些用户的信息
        LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.in(User::getId, followUserIdSet);
        lqwUser.select(User::getUsername, User::getFollowAmount, User::getFansAmount);
        List<User> userList = userService.list(lqwUser);

        List<FollowVO> followVOList = new ArrayList<>();
        for (User user : userList) {
            FollowVO followVO = new FollowVO();
            followVO.setUsername(user.getUsername());
            followVO.setFollowAmount(user.getFollowAmount());
            followVO.setFansAmount(user.getFansAmount());
            followVOList.add(followVO);
        }
        return followVOList;
    }

    @Override
    public List<?> fansQuery(String userId) {
        // 将用户 id 作为订阅者 id 查询
        LambdaQueryWrapper<UserFollow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFollow::getFollowUserId, userId);
        List<UserFollow> userFollowList = this.list(lqw);

        if (CollUtil.isEmpty(userFollowList)) {
            return new ArrayList<>();
        }

        // 将订阅者 id 放在 set 集合中
        Set<Long> fansIdSet = new HashSet<>();
        for (UserFollow userFollow : userFollowList) {
            fansIdSet.add(userFollow.getUserId());
        }
        // 根据订阅者 id 查询这些用户的信息
        LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.in(User::getId, fansIdSet);
        lqwUser.select(User::getUsername, User::getFollowAmount, User::getFansAmount);
        List<User> userList = userService.list(lqwUser);

        List<FollowVO> followVOList = new ArrayList<>();
        for (User user : userList) {
            FollowVO followVO = new FollowVO();
            followVO.setUsername(user.getUsername());
            followVO.setFollowAmount(user.getFollowAmount());
            followVO.setFansAmount(user.getFansAmount());
            followVOList.add(followVO);
        }
        return followVOList;
    }

    @Override
    public void unfollow(String followUserId) {
        LambdaQueryWrapper<UserFollow> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFollow::getUserId, UserContext.getUserId());
        lqw.eq(UserFollow::getFollowUserId, followUserId);
        this.remove(lqw);
    }
}




