package com.it.blog.services;

import com.it.blog.pojo.FriendLink;
import com.it.blog.response.ResponseResult;

public interface IFriendLinkService {
    ResponseResult addFriendLink(FriendLink friendLink);

    ResponseResult deleteFriendLink(String friendLinkId);

    ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink);

    ResponseResult getFriendLink(String friendLinkId);

    ResponseResult listFriendLinks();
}
