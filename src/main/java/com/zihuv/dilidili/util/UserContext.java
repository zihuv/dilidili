package com.zihuv.dilidili.util;

import com.zihuv.dilidili.util.user.IUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    @Autowired
    private IUserContext iUserContext;

    public Long getUserId() {
        return iUserContext.getUserId();
    }

    public String getUserName() {
        return iUserContext.getUsername();
    }
}