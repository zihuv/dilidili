package com.zihuv.dilidili.service.comment;

import com.zihuv.dilidili.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.dilidili.model.param.CommentParam;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 当 parentId > 0，则代表为子评论，回复别人的评论；当 parentId == 0，则代表为父评论，传入的 id 不会被后端使用
     */
    void addComment(CommentParam commentParam);

    List<?> listComment(Long videoId);

    void deleteComment(Long id);
}