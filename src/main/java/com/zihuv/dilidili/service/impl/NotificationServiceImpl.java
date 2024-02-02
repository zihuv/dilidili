package com.zihuv.dilidili.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.model.entity.Notification;
import com.zihuv.dilidili.service.NotificationService;
import com.zihuv.dilidili.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService{

    @Override
    public List<?> listReplyNotification() {

        return null;
    }
}