package com.it.blog.services.impl;

import com.it.blog.pojo.SobUser;
import com.it.blog.services.IUserService;
import com.it.blog.utils.Constants;
import com.it.blog.utils.CookieUtils;
import com.it.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("permission")
public class PermissionService {

    @Autowired
    private IUserService userService;

    /**
     * 判断是不是管理员
     *
     * @return
     */
    public boolean admin() {
        //拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        String tokeKey = CookieUtils.getCookie(request, Constants.User.COOKIC_TOKEN_KEY);
        //没有令牌的key，没有登录，不用往下执行了
        if (TextUtils.isEmpty(tokeKey)) {
            return false;
        }

        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return false;
        }
        if (Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())) {
            //管理员
            return true;
        }
        return false;
    }

}
