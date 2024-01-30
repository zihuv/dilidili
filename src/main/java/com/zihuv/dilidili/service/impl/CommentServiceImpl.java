package com.zihuv.dilidili.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.CommentMapper;
import com.zihuv.dilidili.model.entity.Comment;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.model.param.CommentParam;
import com.zihuv.dilidili.model.vo.CommentVO;
import com.zihuv.dilidili.service.CommentService;
import com.zihuv.dilidili.service.VideoService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private VideoService videoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addComment(CommentParam commentParam) {
        // TODO 添加评论数量
        // TODO 当在评论的时候，父评论可能删除了，存在并发的情况
        // 评论有三种情况：
        // 1. 一级评论：root = parent = 0。当 root = 0 时，请求参数的 parent 不会被使用，而是直接设置为 0
        // 2. 二级评论：root = parent != 0
        // 3. 三级评论：root != parent != 0
        Video video = videoService.getById(commentParam.getVideoId());
        if (video == null) {
            throw new ClientException(StrUtil.format("[评论服务] 点赞的视频 id：{} 不存在", commentParam.getVideoId()));
        }

        boolean isParentComment = commentParam.getRootId() == 0L;
        // 一级评论不回复别人的评论，所以不需要校验回复的评论是否存在
        if (!isParentComment) {
            // 二级评论和三级评论校验根评论是否存在
            if (this.getById(commentParam.getRootId()) == null) {
                throw new ClientException(StrUtil.format("[评论服务] 回复的父评论 id：{} 不存在", commentParam.getRootId()));
            }
            // 三级评论校验父评论
            if (!Objects.equals(commentParam.getRootId(), commentParam.getParentId())
                    && this.getById(commentParam.getParentId()) == null) {
                throw new ClientException(StrUtil.format("[评论服务] 回复的子评论 id：{} 不存在", commentParam.getParentId()));
            }
        }

        Long rootId = isParentComment ? 0L : commentParam.getRootId();
        Long parentId = isParentComment ? 0L : commentParam.getParentId();
        Comment comment = new Comment();
        comment.setRootId(rootId);
        comment.setParentId(parentId);
        comment.setVideoId(commentParam.getVideoId());
        comment.setContentAuthorId(UserContext.getUserId());
        comment.setContent(commentParam.getContent());
        comment.setLikeNum(0L);
        comment.setReplyNum(0L);
        this.save(comment);
    }

    @Override
    public List<?> listComment(Long videoId) {
        // TODO 分页查询
        // TODO 添加缓存
        // step1.查询出所有父评论
        LambdaQueryWrapper<Comment> lqwParentComment = new LambdaQueryWrapper<>();
        lqwParentComment.eq(Comment::getVideoId, videoId);
        lqwParentComment.eq(Comment::getRootId, 0L);
        List<Comment> parentCommentList = this.list(lqwParentComment);
        if (CollUtil.isEmpty(parentCommentList)) {
            return new ArrayList<>();
        }
        List<Long> rootIdList = parentCommentList.stream().map(Comment::getId).toList();

        // step2.查询出所有子评论
        LambdaQueryWrapper<Comment> lqwSonComment = new LambdaQueryWrapper<>();
        lqwSonComment.eq(Comment::getVideoId, videoId);
        lqwSonComment.in(Comment::getRootId, rootIdList);
        List<Comment> sonCommentList = this.list(lqwSonComment);

        // step3.使用 rootId 将父评论与子评论组装
        List<CommentVO> result = new ArrayList<>();
        for (Comment parentComment : parentCommentList) {
            List<CommentVO> sonCommentVOList = new ArrayList<>();
            for (Comment sonComment : sonCommentList) {
                if (parentComment.getId().equals(sonComment.getRootId())) {
                    CommentVO sonCommentVO = new CommentVO();
                    sonCommentVO.setId(sonComment.getId());
                    sonCommentVO.setParentId(sonComment.getParentId());
                    sonCommentVO.setContent(sonComment.getContent());
                    sonCommentVO.setLikeNum(sonComment.getLikeNum());
                    sonCommentVO.setReplyNum(sonComment.getReplyNum());
                    sonCommentVO.setSonComment(null);
                    sonCommentVOList.add(sonCommentVO);
                }
            }
            CommentVO parentCommentVO = new CommentVO();
            parentCommentVO.setId(parentComment.getId());
            parentCommentVO.setParentId(0L);
            parentCommentVO.setContent(parentComment.getContent());
            parentCommentVO.setLikeNum(parentComment.getLikeNum());
            parentCommentVO.setReplyNum(parentComment.getReplyNum());
            parentCommentVO.setSonComment(sonCommentVOList);
            result.add(parentCommentVO);
        }
        return result;
    }

    @Override
    public void deleteComment(Long id) {
        // 两种删除评论的情况：
        // 1.删除子评论：直接删
        // 2.删除父评论：直接删父评论，子评论无需删除，因为在查询的时候只要父评论没被查询出，其对应的子评论也不会被查询
        // TODO 权限校验
        this.removeById(id);
    }

}