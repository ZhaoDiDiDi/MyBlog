package com.it.blog.dao;

import com.it.blog.pojo.Looper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoopDao extends JpaRepository<Looper, String>, JpaSpecificationExecutor<Looper> {
    Looper findOneById(String loopId);

    @Query(nativeQuery = true, value = "select * from `tb_looper` where `state` = ?")
    List<Looper> listLoopByState(String s);
}
