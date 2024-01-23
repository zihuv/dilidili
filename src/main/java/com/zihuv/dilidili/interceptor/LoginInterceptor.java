package com.zihuv.dilidili.interceptor;

import cn.hutool.core.util.StrUtil;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.exception.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是 OPTIONS 请求，直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        // 从请求头中获取 token
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new ClientException("[登录拦截器] token 不能为 null");
        }
        if (redisTemplate.opsForValue().get(RedisConstant.USER_TOKEN_KEY + token) == null) {
            throw new ClientException(StrUtil.format("[登录拦截器] token：{} 不存在", token));
        }
        return true;
    }
}
