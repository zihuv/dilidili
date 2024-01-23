package com.zihuv.dilidili.controller.video;

import com.zihuv.dilidili.model.param.VideoPublishParam;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.video.VideoService;
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

    @GetMapping("/api/video/delete")
    public Result<?> deleteVideo(@RequestParam Long videoId) {
        videoService.deleteVideo(videoId);
        return Result.success();
    }
}