package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.dilidili.model.BaseDO;
import lombok.Data;

@Data
@TableName("tb_comment")
public class Comment extends BaseDO {

    @TableId
    private Long id;

    private Long rootId;

    private Long videoId;

    private Long parentId;

    private Long contentAuthorId;

    // 冗余数据，减少查询次数
    private Long toUserId;

    private String content;

    private Long likeNum;

    private Long replyNum;

}