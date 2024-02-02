package com.zihuv.dilidili.service;

import com.zihuv.dilidili.model.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface NotificationService extends IService<Notification> {

    List<?> listReplyNotification();
}