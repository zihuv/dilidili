package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.vo.FriendVO;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/friend/add")
    public Result<?> addFriend(@RequestParam Long friendId) {
        friendService.addFriend(friendId);
        return Result.success();
    }

    @GetMapping("/friend/list")
    public Result<List<FriendVO>> listFriend() {
        return Result.success(friendService.listFriend());
    }

    /**
     * 心跳。客户端每隔一端时间需要向服务端发送该请求，确定登录状态
     */
    @GetMapping("/friend/heartbeat")
    public Result<?> heartbeat() {
        friendService.heartbeat();
        return Result.success();
    }
}