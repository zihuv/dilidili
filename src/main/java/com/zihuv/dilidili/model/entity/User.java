package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.dilidili.model.BaseDO;
import lombok.Data;

@Data
@TableName("tb_user")
public class User extends BaseDO {

    @TableId
    private Long id;

    private String username;

    private String password;

    private Long followAmount;

    private Long fansAmount;

}