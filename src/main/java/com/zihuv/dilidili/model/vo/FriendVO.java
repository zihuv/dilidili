package com.zihuv.dilidili.model.vo;

import lombok.Data;

@Data
public class FriendVO {

    private Long friendId;

    private String friendName;

    /**
     * 登录状态（0-离线，1-在线）
     */
    private Integer status;
}