package com.zihuv.dilidili.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.UserMapper;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.param.RegisterUserParam;
import com.zihuv.dilidili.model.vo.UserInfoVO;
import com.zihuv.dilidili.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String login(User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, user.getUsername());
        lqw.eq(User::getPassword, user.getPassword());
        User u = this.getOne(lqw);
        if (u == null) {
            throw new ClientException("[用户服务] 该用户不存在");
        }
        // 生成 token
        String token = UUID.fastUUID().toString();
        // <key, value> - <token, user>
        redisTemplate.opsForValue().set(RedisConstant.USER_TOKEN_KEY + token, u);
        return token;
    }

    @Override
    public void register(RegisterUserParam requestParam) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, requestParam.getUsername());
        User u = this.getOne(lqw);
        if (u != null) {
            throw new ClientException("[用户服务] 该用户名已经存在");
        }

        User user = new User();
        user.setUsername(requestParam.getUsername());
        user.setPassword(requestParam.getPassword());
        user.setFollowAmount(0L);
        user.setFansAmount(0L);
        this.save(user);
    }

    @Override
    public UserInfoVO queryUserInfo(Long userId) {
        // TODO 查询用户的视频信息
        User user = this.getById(userId);
        if (user == null) {
            throw new ClientException(StrUtil.format("[用户模块] 用户：{} 不存在", userId));
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(userId);
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setFollowAmount(user.getFollowAmount());
        userInfoVO.setFansAmount(user.getFansAmount());
        return userInfoVO;
    }

}