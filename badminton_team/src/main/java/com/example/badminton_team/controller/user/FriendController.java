package com.example.badminton_team.controller.user;

import com.example.badminton_team.dao.ChatMessageDao;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.ChatMessage;
import com.example.badminton_team.entity.FriendRequest;
import com.example.badminton_team.entity.Friendship;
import com.example.badminton_team.service.FriendService;
import com.example.badminton_team.dto.Result;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/friend")
public class FriendController {
    @Resource
    private FriendService friendService;

    @Resource
    private ChatMessageDao chatMessageDao;

    // 发送好友申请
    @PostMapping("/request")
    public Result<?> sendRequest(@RequestAttribute("uid") Integer uid,
                                 @RequestBody Map<String, Object> body) {
        Integer toUid = (Integer) body.get("toUid");
        String message = (String) body.getOrDefault("message", "");
        friendService.sendRequest(uid, toUid, message);
        return Result.success("申请已发送");
    }

    // 收到的申请
    @GetMapping("/requests")
    public Result<List<FriendRequest>> getRequests(@RequestAttribute("uid") Integer uid) {
        return Result.success(friendService.getMyRequests(uid));
    }

    // 同意申请
    @PutMapping("/request/{id}/accept")
    public Result<?> accept(@RequestAttribute("uid") Integer uid,
                            @PathVariable Integer id) {
        friendService.acceptRequest(id, uid);
        return Result.success("已同意");
    }

    // 拒绝申请
    @PutMapping("/request/{id}/reject")
    public Result<?> reject(@RequestAttribute("uid") Integer uid,
                            @PathVariable Integer id) {
        friendService.rejectRequest(id, uid);
        return Result.success("已拒绝");
    }

    // 好友列表
    @GetMapping("/list")
    public Result<List<Friendship>> getFriends(@RequestAttribute("uid") Integer uid) {
        return Result.success(friendService.getMyFriends(uid));
    }

    //历史聊天
    @GetMapping("/chat/history/{friendUid}")
    public Result<?> getHistory(@RequestAttribute("uid") Integer uid,
                                @PathVariable Integer friendUid,
                                @RequestParam(defaultValue = "50") int limit) {
        List<ChatMessage> msgs = chatMessageDao.findByUsers(uid, friendUid, limit);
        // 倒序变为升序（默认是按时间降序取最近50条，前端展示需要从早到晚）
        java.util.Collections.reverse(msgs);
        return Result.success(msgs);
    }
}