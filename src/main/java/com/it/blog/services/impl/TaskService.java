package com.it.blog.services.impl;

import com.it.blog.utils.EmailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    /**
     * 为什么不直接在EmailSender类开启异步？
     *      在静态方法直接添加会失效，调用的地方和方法的地方需要不同类否则也会失效
     * @param verifyCode
     * @param emailAddress
     * @throws Exception
     */

    @Async
    public void sendEmailVerifyCode(String verifyCode, String emailAddress) throws Exception {
        EmailSender.sendRegisterVerifyCode(verifyCode,emailAddress);
    }
}
