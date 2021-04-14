package com.it.blog.dao;

import com.it.blog.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;


public interface LabelDao extends JpaRepository<Label, String>, JpaSpecificationExecutor<Label> {

    @Modifying
    int deleteOneById(String id);

    /**
     * 根据Id查找标签
     * @param id
     * @return
     */
    Label findOneById(String id);
}
