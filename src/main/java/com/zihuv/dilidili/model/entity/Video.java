package com.zihuv.dilidili.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.dilidili.model.BaseDO;
import lombok.Data;

@Data
@TableName("tb_video")
public class Video extends BaseDO {

    private Long id;

    private String videoTitle;

    private String videoName;

    private String videoPath;

    private String picPath;

    private Integer playAmount;

    private Integer commentAmount;

    private Integer likeAmount;

    private Integer collectAmount;

    private Integer shareAmount;

    private Integer isReviewed;
}