package com.it.blog.controller.portal;

import com.it.blog.pojo.Comment;
import com.it.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {

    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment) {
        return null;
    }

    @PutMapping("/{commentId}")
    public ResponseResult updateComment(@PathVariable("commentId") String commentId) {
        return null;
    }

    @GetMapping("/list/{articleId}")
    public ResponseResult listComments(@PathVariable("articleId") String articleId) {
        return null;
    }
}