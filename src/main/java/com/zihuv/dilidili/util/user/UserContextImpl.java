package com.zihuv.dilidili.util.user;

import cn.hutool.core.util.StrUtil;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.util.JSON;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserContextImpl implements IUserContext {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private HttpServletRequest request;

    @Override
    public String getUsername() {
        return getUserInfo().getUsername();
    }

    @Override
    public Long getUserId() {
        return getUserInfo().getId();
    }

    private User getUserInfo() {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new ClientException("[UserContext] token 为 null");
        }
        Object u = redisTemplate.opsForValue().get(RedisConstant.USER_TOKEN_KEY + token);
        if (u == null) {
            throw new ClientException(StrUtil.format("[UserContext] token：{} 不存在", token));
        }
        return JSON.toBean(u, User.class);
    }
}