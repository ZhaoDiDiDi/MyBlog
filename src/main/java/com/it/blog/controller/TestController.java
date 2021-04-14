package com.it.blog.controller;

import com.it.blog.dao.CommentDao;
import com.it.blog.dao.LabelDao;
import com.it.blog.pojo.Comment;
import com.it.blog.pojo.Label;
import com.it.blog.pojo.SobUser;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.IUserService;
import com.it.blog.utils.Constants;
import com.it.blog.utils.CookieUtils;
import com.it.blog.utils.IdWorker;
import com.it.blog.utils.RedisUtils;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private LabelDao labelDao;

    @GetMapping("/hello_word")
    public ResponseResult hello() {
        String redisContent = (String)redisUtils.get(Constants.User.KEY_CAPTCHA_CONTENT + "123456");
        log.info("redisContent ==> " + redisContent);
        return ResponseResult.SUCCESS("hello_word");
    }

    @PostMapping("/label")
    public ResponseResult addLabel(@RequestBody Label label) {
        //判断数据是否有效
        //补全数据
        label.setId(idWorker.nextId() + "");
        label.setCreateTime(new Date());
        label.setUpdateTime(new Date());
        //保存数据
        labelDao.save(label);

        return ResponseResult.SUCCESS("测试标签添加成功");
    }

    @DeleteMapping("/label/{labelId}")
    public ResponseResult deleteLabel(@PathVariable("labelId") String labelId) {
        int deletResult = labelDao.deleteOneById(labelId);
        log.info("deletResult ==> " + deletResult);
        if (deletResult > 0) {
            return ResponseResult.SUCCESS("删除标签成功");
        } else {
            return ResponseResult.FAILED("标签不存在");
        }
    }

    @PutMapping("/label/{labelId}")
    public ResponseResult updateLabel(@PathVariable("labelId") String labelId,@RequestBody Label label) {
        Label dbLabel = labelDao.findOneById(labelId);
        if (dbLabel == null) {
            return ResponseResult.FAILED("标签不存在");
        }
        dbLabel.setName(label.getName());
        dbLabel.setCount(label.getCount());
        dbLabel.setUpdateTime(new Date());
        labelDao.save(dbLabel);
        return ResponseResult.SUCCESS("标签修改成功");
    }

    @GetMapping("/label/{labelId}")
    public ResponseResult getLabel(@PathVariable("labelId") String labelId) {
        Label dbLabel = labelDao.findOneById(labelId);
        if (dbLabel == null) {
            return ResponseResult.FAILED("标签不存在");
        }
        return ResponseResult.SUCCESS("获取标签成功").setData(dbLabel);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/label/list/{page}/{size}")
    public ResponseResult listLabel(@PathVariable("page") int page, @PathVariable("size") int size) {
        if (page <= 1) {
            page = 1;
        }
        if (size <= 0) {
            size = Constants.DEFAULT_SIZE;
        }
        //排序查询
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");//按照属性名而不是字段
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Label> result = labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取成功").setData(result);
    }

    /**
     * 根据条件查询
     * @param keyword
     * @return
     */
    @GetMapping("label/search")
    public ResponseResult doLabelSearch(@RequestParam("keyword") String keyword,@RequestParam("count") String count) {
        List<Label> all = labelDao.findAll(new Specification<Label>() {
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Predicate namePre = cb.equal(root.get("name").as(String.class), keyword);//根据name查
                Predicate namePre = cb.like(root.get("name").as(String.class), "%" + keyword + "%");//模糊查询
                Predicate countPre = cb.equal(root.get("count").as(String.class), count);//条件联立查询
                Predicate and = cb.and(namePre, countPre);
                return and;
            }
        });
        if (all.size() == 0) {
            return ResponseResult.FAILED("结果为空");
        }
        return ResponseResult.SUCCESS("查找成功").setData(all);
    }

    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        // specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        specCaptcha.setFont(Captcha.FONT_1);
        // 设置类型，纯数字、纯字母、字母数字混合
        //specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info("captcha c" +
                "ontent == > " + content);
        // 验证码存入session
        //request.getSession().setAttribute("captcha", content);
        //保存到redis ,十分钟有效
        redisUtils.set(Constants.User.KEY_CAPTCHA_CONTENT + "123456", content, 60 * 10);
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private IUserService userService;

    @PostMapping("/comment")
    public ResponseResult testComment(@RequestBody Comment comment) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String content = comment.getContent();
        log.info("comment content ==> " + content);
        //还得知道是谁的评论，对评论进行身份确定
        String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIC_TOKEN_KEY);
        if (tokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }

        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        comment.setId(sobUser.getId());
        comment.setUserAvatar(sobUser.getAvatar());
        comment.setUserName(sobUser.getUserName());
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setId(idWorker.nextId() + "");
        commentDao.save(comment);
        return ResponseResult.SUCCESS("评论成功");
    }
}
