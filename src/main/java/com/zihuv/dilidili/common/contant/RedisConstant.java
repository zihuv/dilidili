package com.zihuv.dilidili.common.contant;

public class RedisConstant {

    public static final String USER_TOKEN_KEY = "user:token:";

    // 用户关注人
    public static final String USER_FOLLOW = "user:follow:";

    // 用户粉丝
    public static final String USER_FANS = "user:fans:";

    // 热门排行榜
    public static final String HOT_RANK = "hot:rank";

    // 视频信息 key + videoId
    public static final String VIDEO_INFO = "video:info:";

    // 视频点赞数 key + videoId
    public static final String VIDEO_LIKE = "like:video:";

    // 视频播放数 key + videoId
    public static final String VIDEO_VIEWS = "views:video:";

    // 用户点赞过的视频 key + userId。videoId 使用 ZSET 存储
    public static final String USER_LIKE = "user:like:";

    // 系统通知（数据结构：List）
    public static final String SYSTEM_NOTIFICATION = "notification:system";

    // 朋友登录状态 key + userId
    public static final String FRIEND_STATUS = "friend:status:";
}