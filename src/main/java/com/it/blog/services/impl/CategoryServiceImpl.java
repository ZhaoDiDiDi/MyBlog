package com.it.blog.services.impl;

import com.it.blog.dao.CategoryDao;
import com.it.blog.pojo.Category;
import com.it.blog.pojo.SobUser;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.ICategoryService;
import com.it.blog.services.IUserService;
import com.it.blog.utils.Constants;
import com.it.blog.utils.IdWorker;
import com.it.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl extends BaseService implements ICategoryService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private IUserService userService;

    @Override
    public ResponseResult addCategory(Category category) {
        //先检查数据
        //必须的数据有：分类名称，分类的pinyin、顺序、描述
        if (TextUtils.isEmpty(category.getName())) {
            return ResponseResult.FAILED("分类名称不可以为空");
        }
        if (TextUtils.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED("分类拼音不可以为空");
        }
        if (TextUtils.isEmpty(category.getDescription())) {
            return ResponseResult.FAILED("分类描述不可以为空");
        }
        //补全数据
        category.setId(idWorker.nextId() + "");
        category.setStatus("1");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        //保存数据
        categoryDao.save(category);
        //返回结果
        return ResponseResult.SUCCESS("添加分类成功");
    }

    @Override
    public ResponseResult getCategory(String categoryId) {
        Category category = categoryDao.findOneById(categoryId);
        if (category == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        return ResponseResult.SUCCESS("分类获取成功").setData(category);
    }

    @Override
    public ResponseResult listCategories() {
        //参数检查
        //创建条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "order");
        //判断用户角色，普通 用户/未登录用户，只能获取到正常的category
        //管理员帐户，可以拿到所有的分类.
        SobUser sobUser = userService.checkSobUser();
        List<Category> categories;
        if (sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())) {
            //只能获取到正常的category
            categories = categoryDao.listCategoriesBystatus("1");

        } else {
            //查询
            categories = categoryDao.findAll(sort);
        }
        //返回结果
        return ResponseResult.SUCCESS("获取分类列表成功.").setData(categories);
    }

    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {
        //第一步是找出来
        Category categoryFromDb = categoryDao.findOneById(categoryId);
        if (categoryFromDb == null) {
            return ResponseResult.FAILED("分类不存在.");
        }
        //第二步是对内容判断，有些字段是不可以为空的
        String name = category.getName();
        if (!TextUtils.isEmpty(name)) {
            categoryFromDb.setName(name);
        }
        String pinyin = category.getPinyin();
        if (!TextUtils.isEmpty(pinyin)) {
            categoryFromDb.setPinyin(pinyin);
        }

        String description = category.getDescription();
        if (!TextUtils.isEmpty(description)) {
            categoryFromDb.setDescription(description);
        }
        categoryFromDb.setOrder(category.getOrder());
        categoryFromDb.setUpdateTime(new Date());
        //第三步是保存数据
        categoryDao.save(categoryFromDb);
        //返回结果
        return ResponseResult.SUCCESS("分类更新成功.");
    }

    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = categoryDao.deleteCategoryByUpdateState(categoryId);
        if (result == 0) {
            return ResponseResult.FAILED("该分类不存在.");
        }
        return ResponseResult.SUCCESS("删除分类成功.");
    }


}
