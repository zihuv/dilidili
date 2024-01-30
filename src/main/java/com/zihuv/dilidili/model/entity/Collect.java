package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.dilidili.model.BaseDO;
import lombok.Data;

@Data
@TableName("tb_collect")
public class Collect extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String collectName;

}