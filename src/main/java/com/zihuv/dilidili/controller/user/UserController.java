package com.zihuv.dilidili.controller.user;

import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/user/login")
    public Result<?> login(@RequestParam User user) {
        return Result.success(userService.login(user));
    }
}