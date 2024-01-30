package com.zihuv.dilidili.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class CollectVO {

    private Long collectId;

    private String collectName;

    private List<CollectVideoVO> collectVideoVOList;
}