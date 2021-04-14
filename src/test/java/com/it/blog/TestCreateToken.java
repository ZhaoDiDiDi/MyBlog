package com.it.blog;

import com.it.blog.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

public class TestCreateToken {
    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "722250648279580673");
        claims.put("userName", "测试用户");
        claims.put("role", "role_normal");
        claims.put("avatar", "https://cdn.sunofbeaches.com/images/default_avatar.png");
        claims.put("email", "test@sunofbeach.net");
        String token = JwtUtil.createToken(claims);
        System.out.println(token);
    }
}
