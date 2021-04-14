package com.it.blog.controller.portal;

import com.it.blog.response.ResponseResult;
import com.it.blog.services.IArticleService;
import com.it.blog.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    @Autowired
    private IArticleService articleService;

    /**
     * 获取文章列表
     * 权限，所有用户
     * 状态：必须已经发布的，置顶的由另一个接口获取，其他的不从此接口获取
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page") int page,@PathVariable("size") int size) {
        return articleService.listArticles(page, size, null, null, Constants.Article.STATE_PUBLISH);
    }

    @GetMapping("/list/{categoryId}/{page}/{size}")
    public ResponseResult listArticleByCategoryId(@PathVariable("categoryId") String categoryId,
                                      @PathVariable("page") int page,
                                      @PathVariable("size") int size) {
        return articleService.listArticles(page, size, null, categoryId, Constants.Article.STATE_PUBLISH);
    }

    /**
     * 获取文章的详情
     * 权限：任意用户
     * <p>
     * 内容过滤：只允许拿置顶的或者已经发布的
     * 其他的获取：比如说草稿、只能对应用户获取。已删除的，只有管理员才可以获取
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId") String articleId) {
        return articleService.getArticleById(articleId);
    }

    /**
     * 通过标签来计算这匹配度
     * 标签：有一个，或者多个（5以内，包含5个）
     * 从里面随机拿一个标签出来
     * @param articleId
     * @return
     */
    @GetMapping("/recomment/{articleId}")
    public ResponseResult getRecommentArticles(@PathVariable("article") String articleId) {
        return null;
    }

    @GetMapping("/top")
    public ResponseResult getTopArticle() {
        return articleService.listTopArticles();
    }
}
