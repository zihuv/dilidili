package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    @GetMapping("/api/user/follow")
    public Result<?> follow(@RequestParam Long subscribeId) {
        userFollowService.follow(subscribeId);
        return Result.success();
    }

    @GetMapping("/api/user/follow/query")
    public Result<?> followQuery(@RequestParam String userId) {
        List<?> followQueryList = userFollowService.followQuery(userId);
        return Result.success(followQueryList);
    }

    /**
     * 取关
     */
    @GetMapping("/api/user/unfollow")
    public Result<?> unfollow(@RequestParam String followUserId) {
        userFollowService.unfollow(followUserId);
        return Result.success();
    }

    @GetMapping("/api/user/fans/query")
    public Result<?> fansQuery(@RequestParam String userId) {
        return Result.success(userFollowService.fansQuery(userId));
    }
}
