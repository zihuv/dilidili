package com.zihuv.dilidili.model.param;

import lombok.Data;

@Data
public class CommentParam {

    private Long videoId;

    private String content;

    private Long rootId;

    private Long parentId;
}