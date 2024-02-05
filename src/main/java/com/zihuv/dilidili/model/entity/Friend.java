package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_friend")
public class Friend {

    @TableId
    private Long id;

    private Long userId;

    private Long friendId;

}