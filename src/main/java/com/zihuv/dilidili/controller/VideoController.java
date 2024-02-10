package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.param.VideoPublishParam;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/api/video/token/get")
    public Result<?> getUploadVideoToken() {
        String token = videoService.getUploadVideoToken();
        return Result.success(token);
    }

    @PostMapping("/api/video/publish")
    public Result<?> publishVideo(@RequestBody VideoPublishParam videoPublishParam) {
        videoService.publishVideo(videoPublishParam);
        return Result.success();
    }

    @GetMapping("/api/video/get")
    public Result<?> getVideoByName(@RequestParam String videoTitle) {
        return Result.success(videoService.getVideoByName(videoTitle));
    }

    @GetMapping("/api/video/get/id")
    public Result<?> getVideoByName(@RequestParam Long videoId) {
        return Result.success(videoService.getVideoById(videoId));
    }

    @GetMapping("/api/video/delete")
    public Result<?> deleteVideo(@RequestParam Long videoId) {
        videoService.deleteVideo(videoId);
        return Result.success();
    }

    /**
     * 获取热度排行榜视频
     */
    @GetMapping("/api/video/hot/rank")
    public Result<?> getHotRankVideo() {
        return Result.success(videoService.getHotRankVideo());
    }

    /**
     * 添加视频播放量
     */
    @GetMapping("/api/video/views/increment")
    public Result<?> incrementVideoViews(@RequestParam Long videoId) {
        videoService.incrementVideoViews(videoId);
        return Result.success();
    }
}