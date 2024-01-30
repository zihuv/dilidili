package com.zihuv.dilidili.service.comment.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.comment.CommentMapper;
import com.zihuv.dilidili.model.entity.Comment;
import com.zihuv.dilidili.model.entity.CommentRelation;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.model.param.CommentParam;
import com.zihuv.dilidili.model.vo.CommentVO;
import com.zihuv.dilidili.service.comment.CommentRelationService;
import com.zihuv.dilidili.service.comment.CommentService;
import com.zihuv.dilidili.service.video.VideoService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserContext userContext;

    @Autowired
    private CommentRelationService commentRelationService;

    @Autowired
    private VideoService videoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addComment(CommentParam commentParam) {
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

        Comment comment = new Comment();
        comment.setContentAuthorId(userContext.getUserId());
        comment.setContent(commentParam.getContent());
        comment.setLikeNum(0L);
        comment.setReplyNum(0L);
        this.save(comment);

        Long parentId = isParentComment ? 0L : commentParam.getParentId();
        Long rootId = isParentComment ? 0L : commentParam.getRootId();
        CommentRelation commentRelation = new CommentRelation();
        // 评论中间表和评论表属于一对一关系，评论 id = 评论中间表 id
        commentRelation.setId(comment.getId());
        commentRelation.setVideoId(commentParam.getVideoId());
        commentRelation.setRootId(rootId);
        commentRelation.setParentId(parentId);
        commentRelationService.save(commentRelation);
    }

    @Override
    public List<?> listComment(Long videoId) {
        // TODO 分页查询
        // TODO 性能优化
        // step1.查询出所有父评论
        LambdaQueryWrapper<CommentRelation> lqwParentRelation = new LambdaQueryWrapper<>();
        lqwParentRelation.eq(CommentRelation::getVideoId, videoId);
        lqwParentRelation.eq(CommentRelation::getRootId, 0L);
        lqwParentRelation.select(CommentRelation::getId);
        List<CommentRelation> parentCommentRelationList = commentRelationService.list(lqwParentRelation);
        if (CollUtil.isEmpty(parentCommentRelationList)) {
            return new ArrayList<>();
        }
        List<Long> parentIds = parentCommentRelationList.stream().map(CommentRelation::getId).toList();

        // step2.查询出所有子评论
        LambdaQueryWrapper<CommentRelation> lqwSonRelation = new LambdaQueryWrapper<>();
        lqwSonRelation.eq(CommentRelation::getVideoId, videoId);
        lqwSonRelation.in(CommentRelation::getRootId, parentIds);
        List<CommentRelation> sonCommentRelationList = commentRelationService.list(lqwSonRelation);

        // step3.使用 rootId 和 parentId 将子评论与父评论组装成 id 树
        Map<Long, List<Long>> commentIdTree = new LinkedHashMap<>();
        for (CommentRelation pRelation : parentCommentRelationList) {
            for (CommentRelation sRelation : sonCommentRelationList) {
                if (pRelation.getId().equals(sRelation.getRootId())) {
                    List<Long> sonList = commentIdTree.get(pRelation.getId());
                    if (sonList == null) {
                        List<Long> idList = new ArrayList<>();
                        idList.add(sRelation.getId());
                        commentIdTree.put(pRelation.getId(), idList);
                    } else {
                        sonList.add(sRelation.getId());
                    }
                }
            }
        }
        // step4.根据 id 树来组装父子评论
        List<CommentVO> result = new ArrayList<>();
        commentIdTree.forEach((parentId, sonIdList) -> {
            Comment parentComment = this.getById(parentId);
            LambdaQueryWrapper<Comment> lqwSonComment = new LambdaQueryWrapper<>();
            lqwSonComment.in(Comment::getId, sonIdList);
            List<Comment> sonCommentList = this.list(lqwSonComment);

            List<CommentVO> sonCommenVOtList = new ArrayList<>();
            for (Comment comment : sonCommentList) {
                CommentVO commentVO = new CommentVO();
                commentVO.setId(comment.getId());
                commentVO.setParentId(0L); // TODO parentId 有问题
                commentVO.setContent(comment.getContent());
                commentVO.setLikeNum(comment.getLikeNum());
                commentVO.setReplyNum(comment.getReplyNum());
                commentVO.setSonComment(null);
                sonCommenVOtList.add(commentVO);
            }
            CommentVO parentCommentVO = new CommentVO();
            parentCommentVO.setId(parentComment.getId());
            parentCommentVO.setParentId(0L);
            parentCommentVO.setContent(parentComment.getContent());
            parentCommentVO.setLikeNum(parentComment.getLikeNum());
            parentCommentVO.setReplyNum(parentComment.getReplyNum());
            parentCommentVO.setSonComment(sonCommenVOtList);
            result.add(parentCommentVO);
        });
        return result;
    }

}