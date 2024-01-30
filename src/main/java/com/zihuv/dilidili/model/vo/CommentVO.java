package com.zihuv.dilidili.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class CommentVO {

    private Long id;

    private Long parentId;

    private String content;

    private Long likeNum;

    private Long replyNum;

    private List<CommentVO> sonComment;
}