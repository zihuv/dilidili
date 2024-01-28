package com.zihuv.dilidili.controller.like;

import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.like.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 查询用户点赞过的视频
     */
    @GetMapping("/api/like/list")
    public Result<?> listLikeVideo() {
        return Result.success(likeService.listLikeVideo());
    }

    /**
     * 点赞视频（之前未点赞则为点赞，否则为取消点赞）
     */
    @GetMapping("/api/like/video")
    public Result<?> likeVideo(@RequestParam Long videoId) {
        likeService.likeVideo(videoId);
        return Result.success();
    }
}
