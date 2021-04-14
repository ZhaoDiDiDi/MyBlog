package com.it.blog;

import org.springframework.util.DigestUtils;

public class TestCreateJwtMd5VALUES {
    public static void main(String[] args) {
        String jwtKeyMd5tr = DigestUtils.md5DigestAsHex("sob_blog_system_-=".getBytes());
        System.out.println(jwtKeyMd5tr);//ad128433d8e3356e7024009bf6add2ab
    }
}
