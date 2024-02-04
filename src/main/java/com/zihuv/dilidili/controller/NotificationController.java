package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.param.PostSystemNotificationParam;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 查询所有“回复我的”通知
     */
    @GetMapping("/api/notify/reply/list")
    public Result<?> listReplyNotification() {
        return Result.success(notificationService.listReplyNotification());
    }

    /**
     * TODO 查询所有“收到的赞”通知（视频点赞和评论点赞）
     */
    @GetMapping("/api/notify/like/list")
    public Result<?> listLikeNotification() {
        return Result.success();
    }


    /**
     * 查询所有历史通知（拉消息）
     */
    @GetMapping("/api/notify/system/list")
    public Result<?> listSystemNotification() {
        return Result.success(notificationService.listSystemNotification());
    }

    /**
     * 查询所有消息未读数
     */
    @GetMapping("/api/notify/unread/count")
    public Result<?> getUnreadCount() {
        return Result.success();
    }

    /**
     * 发布系统通知
     */
    @PostMapping("/api/notify/system/post")
    public Result<?> postSystemNotification(@RequestBody PostSystemNotificationParam requestParam) {
        notificationService.postSystemNotification(requestParam);
        return Result.success();
    }

}