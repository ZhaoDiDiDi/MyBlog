package com.it.blog.dao;

import com.it.blog.pojo.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendLinkDao extends JpaRepository<FriendLink, String>, JpaSpecificationExecutor<FriendLink> {

    FriendLink findOneById(String friendLinkId);

    @Query(value = "select * from `tb_friends` where `state` = ? ", nativeQuery = true)
    List<FriendLink> listFriendLinkByState(String s);

    int deleteAllById(String friendLinkId);
}
