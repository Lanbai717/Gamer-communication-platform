package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.FriendRequestDao;
import com.example.badminton_team.dao.FriendshipDao;
import com.example.badminton_team.entity.FriendRequest;
import com.example.badminton_team.entity.Friendship;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.FriendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    @Resource
    private FriendRequestDao friendRequestDao;

    @Resource
    private FriendshipDao friendshipDao;

    @Override
    @Transactional
    public void sendRequest(Integer fromUid, Integer toUid, String message) {
        // 不能添加自己
        if (fromUid.equals(toUid)) {
            throw new BusinessException("不能添加自己为好友");
        }
        // 检查是否已是好友
        Friendship exist = friendshipDao.findByPair(fromUid, toUid);
        if (exist != null) {
            throw new BusinessException("你们已经是好友了");
        }
        // 检查是否已有未处理的申请（自己发出的）
        FriendRequest pending = friendRequestDao.findPending(fromUid, toUid);
        if (pending != null) {
            throw new BusinessException("已发送过申请，请等待对方处理");
        }
        // 检查对方是否已给自己发过申请（反向申请）
        FriendRequest reverse = friendRequestDao.findPending(toUid, fromUid);
        if (reverse != null) {
            throw new BusinessException("对方已向你发送了好友申请，请先处理");
        }
        // 创建申请
        FriendRequest request = new FriendRequest();
        request.setFromUid(fromUid);
        request.setToUid(toUid);
        request.setMessage(message);
        request.setStatus(0);
        friendRequestDao.insert(request);
    }

    @Override
    public List<FriendRequest> getMyRequests(Integer uid) {
        return friendRequestDao.findByToUid(uid);
    }

    @Override
    @Transactional
    public void acceptRequest(Integer requestId, Integer currentUid) {
        FriendRequest request = friendRequestDao.findById(requestId);
        if (request == null || !request.getToUid().equals(currentUid)) {
            throw new BusinessException("无权操作此申请");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException("该申请已被处理");
        }
        // 更新申请状态为同意
        friendRequestDao.updateStatus(requestId, 1);
        // 建立双向好友关系（保持小 uid 在前）
        int uid1 = Math.min(request.getFromUid(), request.getToUid());
        int uid2 = Math.max(request.getFromUid(), request.getToUid());
        Friendship friendship = new Friendship();
        friendship.setUserId1(uid1);
        friendship.setUserId2(uid2);
        friendshipDao.insert(friendship);
    }

    @Override
    public void rejectRequest(Integer requestId, Integer currentUid) {
        FriendRequest request = friendRequestDao.findById(requestId);
        if (request == null || !request.getToUid().equals(currentUid)) {
            throw new BusinessException("无权操作此申请");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException("该申请已被处理");
        }
        friendRequestDao.updateStatus(requestId, 2);
    }

    @Override
    public List<Friendship> getMyFriends(Integer uid) {
        return friendshipDao.findByUserId(uid);
    }
}