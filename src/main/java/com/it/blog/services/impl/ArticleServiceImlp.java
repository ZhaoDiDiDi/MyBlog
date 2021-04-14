package com.it.blog.services.impl;

import com.it.blog.dao.ArticleDao;
import com.it.blog.dao.ArticleNoContentDao;
import com.it.blog.pojo.Article;
import com.it.blog.pojo.ArticleNoContent;
import com.it.blog.pojo.SobUser;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.IArticleService;
import com.it.blog.services.IUserService;
import com.it.blog.utils.Constants;
import com.it.blog.utils.IdWorker;
import com.it.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ArticleServiceImlp extends BaseService implements IArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleNoContentDao articleNoContentDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private IdWorker idWorker;

    /**
     * 后期可以去做一些定时发布的功能
     * 如果是多人博客系统，得考虑审核的问题--->成功,通知，审核不通过，也可通知
     * <p>
     * 保存成草稿
     * 1、用户手动提交：会发生页面跳转-->提交完即可
     * 2、代码自动提交，每隔一段时间就会提交-->不会发生页面跳转-->多次提交-->如果没有唯一标识，会就重添加到数据库里
     * <p>
     * 不管是哪种草稿-->必须有标题
     * <p>
     * 方案一：每次用户发新文章之前-->先向后台请求一个唯一文章ID
     * 如果是更新文件，则不需要请求这个唯一的ID
     * <p>
     * 方案二：可以直接提交，后台判断有没有ID,如果没有ID，就新创建，并且ID作为此次返回的结果
     * 如果有ID，就修改已经存在的内容。
     * <p>
     * 推荐做法：
     * 自动保存草稿，在前端本地完成，也就是保存在本地。
     * 如果是用户手动提交的，就提交到后台
     *
     *
     * <p>
     * 防止重复提交（网络卡顿的时候，用户点了几次提交）：
     * 可以通过ID的方式
     * 通过token_key的提交频率来计算，如果30秒之内有多次提交，只有最前的一次有效
     * 其他的提交，直接return,提示用户不要太频繁操作.
     * <p>
     * 前端的处理：点击了提交以后，禁止按钮可以使用，等到有响应结果，再改变按钮的状态.
     *
     * @param article
     * @return
     */
    @Override
    public ResponseResult postArticle(Article article) {
        //检查用户，获取到用户对象
        SobUser sobUser = userService.checkSobUser();
        //未登录
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }

        //检查数据
        //title、分类ID、内容、类型、摘要、标签
        String title = article.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("标题不可以为空.");
        }

        //2种，草稿和发布
        String state = article.getState();
        if (!Constants.Article.STATE_PUBLISH.equals(state) &&
                !Constants.Article.STATE_DRAFT.equals(state)) {
            //不支持此操作
            return ResponseResult.FAILED("不支持此操作");
        }

        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("类型不可以为空.");
        }

        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("类型格式不正确.");
        }
        //以下是发布的检查，草稿不需要检查
        if (Constants.Article.STATE_PUBLISH.equals(article.getState())) {
            if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
                return ResponseResult.FAILED("文章标题不可超过" + Constants.Article.TITLE_MAX_LENGTH + "个字符");
            }

            String content = article.getContent();
            if (TextUtils.isEmpty(content)) {
                return ResponseResult.FAILED("内容不可以为空.");
            }

            String summary = article.getSummary();
            if (TextUtils.isEmpty(summary)) {
                return ResponseResult.FAILED("摘要不可以为空.");
            }
            if (summary.length() > Constants.Article.SUMMARY_MAX_LENGTH) {
                return ResponseResult.FAILED("文章摘要不可超过" + Constants.Article.SUMMARY_MAX_LENGTH + "个字符");
            }
            String labels = article.getLabel();
            //标签-标签1-标签2
            if (TextUtils.isEmpty(labels)) {
                return ResponseResult.FAILED("标签不可以为空.");
            }
        }

        String articleId = article.getId();
        if (TextUtils.isEmpty(articleId)) {
            //未发布过
            //补充数据：ID、创建时间、用户ID、更新时间
            article.setId(idWorker.nextId() + "");
            article.setCreateTime(new Date());
        } else {
            //已有ID，为更新内容，如果是已经发布的，则不能保存为草稿
            Article articleFromDb = articleDao.findOneById(articleId);
            if (Constants.Article.STATE_PUBLISH.equals(articleFromDb.getState()) &&
                    Constants.Article.STATE_DRAFT.equals(state)) {
                //已发布的只能更新，不能保存草稿
                return ResponseResult.FAILED("已发布文章不支持成为草稿");
            }
        }
        article.setUserId(sobUser.getId());
        article.setUpdateTime(new Date());
        //保存
        articleDao.save(article);

        //TODO:保存到搜索的数据里
        //返回结果,只有一种case使用到这个ID
        //如果需要程序自动保存成草稿（比如说每30秒保存一次，就需要加上这个ID了，否则会创建多个Item）
        return ResponseResult.SUCCESS(
                Constants.Article.STATE_DRAFT.equals(state)?"草稿保存成功.":"文章发表成功."
        ).setData(article.getId());
    }

    /**
     * 获取文章列表
     * @param page       页码
     * @param size       每一页数量
     * @param keyword    标题关键字（搜索关键字）
     * @param categoryId 分类ID
     * @param state      状态：删除、发布、草稿、置顶
     * @return
     */
    @Override
    public ResponseResult listArticles(int page, int size, String keyword,
                                       String categoryId, String state) {
        //处理size、page
        page = chcekSize(page);
        size = chcekSize(size);
        //创建分页和排序条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);//分页信息
        //开始查询
        Page<ArticleNoContent> all = articleNoContentDao.findAll(new Specification<ArticleNoContent>() {
            @Override
            public Predicate toPredicate(Root<ArticleNoContent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                ArrayList<Predicate> predicate = new ArrayList<>();
                //判断是否有传参数
                if (!TextUtils.isEmpty(state)) {
                    Predicate statePre = cb.equal(root.get("state").as(String.class), state);
                    predicate.add(statePre);
                }
                if (!TextUtils.isEmpty(categoryId)) {
                    Predicate categoryIdPre = cb.equal(root.get("categoryId").as(String.class), categoryId);
                    predicate.add(categoryIdPre);
                }
                if (!TextUtils.isEmpty(keyword)) {
                    Predicate keywordPre = cb.like(root.get("title").as(String.class), "%" + keyword + "%");
                    predicate.add(keywordPre);
                }
                Predicate[] preArray = new Predicate[predicate.size()];
                predicate.toArray(preArray);//集合转数组
                return cb.and(preArray);
            }
        }, pageable);
        return ResponseResult.SUCCESS("获取文章列表成功").setData(all);
    }

    /**
     * 如果有审核机制：审核中的文章-->只有管理员和作者自己可以获取
     * 有草稿、删除、置顶的、已经发布的
     * 删除的不能获取，其他的都可以获取
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult getArticleById(String articleId) {
        //查询文章
        Article articleFromDb = articleDao.findOneById(articleId);
        if (articleFromDb == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        //判断文章状态
        String state = articleFromDb.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state) || Constants.Article.STATE_TOP.equals(state)) {
            //可以返回
            return ResponseResult.SUCCESS("文章获取成功").setData(articleFromDb);
        }
        //如果是删除/草稿，需要管理员角色
        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())) {
            return ResponseResult.PERMISSION_DENIED();
        }
        //返回结果
        return ResponseResult.SUCCESS("文章获取成功").setData(articleFromDb);
    }

    /**
     * 更新文章内容
     * <p>
     * 该接口只支持修改内容：标题、内容、标签、分类、摘要
     * @param articleId 文章Id
     * @param article   文章实体
     * @return
     */
    @Override
    public ResponseResult updateArticle(String articleId, Article article) {
        //先找出来
        Article articleFromDb = articleDao.findOneById(articleId);
        if (article == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        //内容修改
        String title = article.getTitle();
        if (!TextUtils.isEmpty(title)) {
            articleFromDb.setTitle(title);
        }
        String content = article.getContent();
        if (!TextUtils.isEmpty(content)) {
            articleFromDb.setContent(content);
        }
        String summary = article.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            articleFromDb.setSummary(summary);
        }
        String categoryId = article.getCategoryId();
        if (!TextUtils.isEmpty(categoryId)) {
            articleFromDb.setCategoryId(categoryId);
        }
        String label = article.getLabel();
        if (!TextUtils.isEmpty(label)) {
            articleFromDb.setLabel(label);
        }
        articleFromDb.setCover(article.getCover());
        articleFromDb.setUpdateTime(new Date());
        articleDao.save(articleFromDb);
        //返回结果
        return ResponseResult.SUCCESS("文章更新成功");
    }

    /**
     * 删除文章 物理删除
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteArticleById(String articleId) {
        int result = articleDao.deleteAllById(articleId);
        if (result > 0) {
            return ResponseResult.FAILED("文章删除成功");
        }
        return ResponseResult.FAILED("文章不存在");
    }

    /**
     * 通过修改状态删除文章，标记删除
     *
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteArticleByState(String articleId) {
        int result = articleDao.deleteArticleByState(articleId);
        if (result > 0) {
            return ResponseResult.SUCCESS("文章删除成功.");
        }
        return ResponseResult.FAILED("文章不存在.");
    }

    @Override
    public ResponseResult topArticle(String articleId) {
        //必须已经发布的，才可以置顶
        Article article = articleDao.findOneById(articleId);
        if (article == null) {
            return ResponseResult.FAILED("文章不存在.");
        }
        String state = article.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state)) {
            article.setState(Constants.Article.STATE_TOP);
            articleDao.save(article);
            return ResponseResult.SUCCESS("文章置顶成功.");
        }
        if (Constants.Article.STATE_TOP.equals(state)) {
            article.setState(Constants.Article.STATE_PUBLISH);
            articleDao.save(article);
            return ResponseResult.SUCCESS("已取消置顶.");
        }
        return ResponseResult.FAILED("不支持该操作.");
    }

    /**
     * 获取置顶文章
     * 与权限无关
     * 状态必须置顶
     * @return
     */
    @Override
    public ResponseResult listTopArticles() {
        List<Article> all = articleDao.findAllByState(Constants.Article.STATE_TOP);
        return ResponseResult.SUCCESS("文章置顶成功.").setData(all);
    }
}
