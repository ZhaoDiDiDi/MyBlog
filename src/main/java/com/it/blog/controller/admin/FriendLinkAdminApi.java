package com.it.blog.controller.admin;

import com.it.blog.pojo.FriendLink;
import com.it.blog.response.ResponseResult;
import com.it.blog.services.IFriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkAdminApi {

    @Autowired
    private IFriendLinkService friendLinkService;

    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addFriendsLink(@RequestBody FriendLink friendLink) {
        return friendLinkService.addFriendLink(friendLink);
    }

    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{friendLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkId") String friendLinkId) {
        return friendLinkService.deleteFriendLink(friendLinkId);
    }

    //@CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{friendLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendLinkId") String friendLinkId,
                                           @RequestBody FriendLink friendLink) {
        return friendLinkService.updateFriendLink(friendLinkId, friendLink);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendLinkId") String friendLinkId) {
        return friendLinkService.getFriendLink(friendLinkId);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listFriendLinks() {
        return friendLinkService.listFriendLinks();
    }
}