package com.it.blog.controller.portal;

import com.it.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/search")
public class SeachPortalApi {
    @GetMapping
    public ResponseResult doSearch(@RequestParam("keyword") String keyword, @RequestParam("page") int page) {
        return null;
    }
}
