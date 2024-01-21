package com.zihuv.dilidili.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.dilidili.model.entity.User;
import com.zihuv.dilidili.model.param.RegisterUserParam;
import com.zihuv.dilidili.model.vo.UserInfoVO;

public interface UserService extends IService<User> {
    String login(User user);

    void register(RegisterUserParam requestParam);

    UserInfoVO queryUserInfo(Long userId);
}
