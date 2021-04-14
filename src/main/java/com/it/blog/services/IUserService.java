package com.it.blog.services;

import com.it.blog.pojo.SobUser;
import com.it.blog.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response, String captcha_key) throws Exception;

    ResponseResult sendEmail(String type, HttpServletRequest request, String emailAddress);

    ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request);

    ResponseResult doLogin(String captcha, String captchakey, SobUser sobUser);

    SobUser checkSobUser();

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(String userId, SobUser sobUser);

    ResponseResult deleteUserById(String userId);

    ResponseResult listUsers(int page, int size);

    ResponseResult updateUserPassword(String verifyCode, SobUser sobUser);

    ResponseResult updateEmail(String email, String verifyCode);

    ResponseResult doLoginOut();
}
