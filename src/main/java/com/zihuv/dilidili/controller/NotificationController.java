package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

}