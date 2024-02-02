package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_notification")
public class Notification {

    @TableId
    private Long id;

    private Long userId;

    private Long replyUserId;

    private String originalComment;

    private String replyComment;

    private Integer isRead;

}