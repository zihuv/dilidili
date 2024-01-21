package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_user_follow")
public class UserFollow {

    @TableId
    private Long id;

    private Long userId;

    private Long followUserId;

}