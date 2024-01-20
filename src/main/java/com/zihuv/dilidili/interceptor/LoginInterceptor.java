package com.zihuv.dilidili.interceptor;

import cn.hutool.core.util.StrUtil;
import com.zihuv.dilidili.exception.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取JWT
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new ClientException("[登录拦截器] token 不能为 null");
        }
        if (redisTemplate.opsForValue().get(token) == null) {
            throw new ClientException(StrUtil.format("[登录拦截器] token：{} 不存在", token));
        }
        return true;
    }
}
