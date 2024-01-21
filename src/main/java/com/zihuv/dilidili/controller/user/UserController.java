package com.zihuv.dilidili.controller.user;

import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.param.RegisterUserParam;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/user/register")
    public Result<?> register(@RequestBody RegisterUserParam requestParam) {
        userService.register(requestParam);
        return Result.success();
    }

    @PostMapping("/api/user/login")
    public Result<?> login(@RequestBody User user) {
        return Result.success(userService.login(user));
    }

    @GetMapping("/api/user/info")
    public Result<?> queryUserInfo(@RequestParam Long userId) {
        return Result.success(userService.queryUserInfo(userId));
    }
}