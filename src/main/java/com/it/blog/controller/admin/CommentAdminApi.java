package com.it.blog.controller.admin;

import com.it.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {

    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId) {
        return null;
    }


    @GetMapping("/list")
    public ResponseResult listComment(@RequestParam("page") int page,@RequestParam("size") int size) {
        return null;
    }

    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId") String commentId) {
        return null;
    }
}