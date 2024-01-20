package com.zihuv.dilidili.service.user.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.loader.LazyLoader;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.Cache;
import com.zihuv.dilidili.common.contant.UserRedisConstant;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.UserMapper;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.service.user.UserService;
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
        lqw.eq(User::getUsername,user.getUsername());
        lqw.eq(User::getPassword,user.getPassword());
        User u = this.getOne(lqw);
        if (u == null) {
            throw new ClientException("[用户服务] 该用户不存在");
        }
        // 生成 token
        String token = UUID.fastUUID().toString();
        // <key, value> - <token, user>
        redisTemplate.opsForValue().set(UserRedisConstant.USER_TOKEN_KEY + token, u);
        return token;
    }
}