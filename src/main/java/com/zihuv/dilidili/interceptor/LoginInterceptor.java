package com.zihuv.dilidili.interceptor;

import cn.hutool.core.util.StrUtil;
import com.zihuv.dilidili.common.contant.RedisConstant;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.util.JSON;
import com.zihuv.dilidili.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StrUtil.isEmpty(token)) {
            setResponse(response, "[登录拦截器] token 不能为 null");
            return false;
        }
        Object u = redisTemplate.opsForValue().get(RedisConstant.USER_TOKEN_KEY + token);
        if (u == null) {
            setResponse(response, StrUtil.format("[登录拦截器] token：{} 不存在", token));
            return false;
        }

        User user = JSON.toBean(u, User.class);
        UserContext.setUserId(user.getId());
        return true;
    }

    private void setResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSON.toJsonStr(Result.fail(message)));
        response.getWriter().flush();
    }
}
