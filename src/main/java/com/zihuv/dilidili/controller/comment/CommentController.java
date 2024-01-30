package com.zihuv.dilidili.controller.comment;

import com.zihuv.dilidili.model.param.CommentParam;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/api/comment/add")
    public Result<?> addComment(@RequestBody CommentParam commentParam) {
        commentService.addComment(commentParam);
        return Result.success();
    }

    @GetMapping("/api/comment/list")
    public Result<?> listComment(@RequestParam Long videoId) {
        return Result.success(commentService.listComment(videoId));
    }
}
