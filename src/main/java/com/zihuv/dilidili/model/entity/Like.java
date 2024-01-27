package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_like")
public class Like {

    @TableId
    private Long id;

    private Long videoId;

    private Long likeUserId;

}