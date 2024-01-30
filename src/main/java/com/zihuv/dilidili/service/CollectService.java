package com.zihuv.dilidili.service;

import com.zihuv.dilidili.model.entity.Collect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.dilidili.model.param.CollectParam;

import java.util.List;

public interface CollectService extends IService<Collect> {

    void createCollect(String collectName);

    void collectVideo(CollectParam collectParam);

    List<?> listCollect();
}