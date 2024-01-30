package com.zihuv.dilidili.controller;

import com.zihuv.dilidili.model.param.CollectParam;
import com.zihuv.dilidili.model.vo.Result;
import com.zihuv.dilidili.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectController {

    @Autowired
    private CollectService collectService;

    @GetMapping("/api/collect/create")
    public Result<?> createCollect(@RequestParam String collectName) {
        collectService.createCollect(collectName);
        return Result.success();
    }

    @PostMapping("/api/collect/video")
    public Result<?> collectVideo(@RequestBody CollectParam collectParam) {
        collectService.collectVideo(collectParam);
        return Result.success();
    }

    @GetMapping("/api/collect/list")
    public Result<?> listCollect() {
        return Result.success(collectService.listCollect());
    }
}