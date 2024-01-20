package com.zihuv.dilidili.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.dilidili.model.entity.User;

public interface UserService extends IService<User> {
    String login(User user);
}
