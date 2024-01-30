package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_collect_relation")
public class CollectRelation {

    @TableId
    private Long id;

    private Long collectId;

    private Long videoId;

    // 冗余字段，方便校验用户是否收藏过该视频
    private Long userId;

}