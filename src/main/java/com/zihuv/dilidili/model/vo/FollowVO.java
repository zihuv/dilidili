package com.zihuv.dilidili.model.vo;

import lombok.Data;

@Data
public class FollowVO {

    private String username;

    private Long followAmount;

    private Long fansAmount;
}