package com.example.badminton_team.service;

import com.example.badminton_team.entity.FriendRequest;
import com.example.badminton_team.entity.Friendship;

import java.util.List;

public interface FriendService {
    void sendRequest(Integer fromUid, Integer toUid, String message);
    List<FriendRequest> getMyRequests(Integer uid);
    void acceptRequest(Integer requestId, Integer currentUid);
    void rejectRequest(Integer requestId, Integer currentUid);
    List<Friendship> getMyFriends(Integer uid);
}