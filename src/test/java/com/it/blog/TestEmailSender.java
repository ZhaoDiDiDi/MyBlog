package com.it.blog;

import com.it.blog.utils.EmailSender;

import javax.mail.MessagingException;

public class TestEmailSender {
    public static void main(String[] args) throws MessagingException {
        EmailSender.subject("测试发送")
                .from("zhao")
                .text("测试发送内容：asdas")
                .to("739333160@qq.com")
                .send();
    }
}
