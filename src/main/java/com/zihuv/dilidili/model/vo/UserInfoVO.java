package com.zihuv.dilidili.model.vo;

import lombok.Data;

@Data
public class UserInfoVO {

    private Long id;

    private String username;

    private Long followAmount;

    private Long fansAmount;
}