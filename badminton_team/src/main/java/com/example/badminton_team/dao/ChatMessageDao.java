package com.example.badminton_team.dao;

import com.example.badminton_team.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMessageDao {
    int insert(ChatMessage msg);
    List<ChatMessage> findByUsers(@Param("uid1") Integer uid1, @Param("uid2") Integer uid2, @Param("limit") int limit);
    int markRead(@Param("senderUid") Integer senderUid, @Param("receiverUid") Integer receiverUid);
}