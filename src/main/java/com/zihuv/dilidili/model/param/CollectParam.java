package com.zihuv.dilidili.model.param;

import lombok.Data;

import java.util.List;

@Data
public class CollectParam {

    private Long videoId;

    private List<Long> collectIdList;

}