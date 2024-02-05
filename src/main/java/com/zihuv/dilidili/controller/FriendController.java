package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<?> listFriend() {
        return Result.success(friendService.listFriend());
    }

}