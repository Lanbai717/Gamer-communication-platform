package com.example.badminton_team.dao;

import com.example.badminton_team.entity.FriendRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FriendRequestDao {
    int insert(FriendRequest request);
    FriendRequest findById(@Param("id") Integer id);
    List<FriendRequest> findByToUid(@Param("toUid") Integer toUid);
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    // 检查是否已有未处理的申请
    FriendRequest findPending(@Param("fromUid") Integer fromUid, @Param("toUid") Integer toUid);
}