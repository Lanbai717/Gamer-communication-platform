package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Friendship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FriendshipDao {
    int insert(Friendship friendship);
    List<Friendship> findByUserId(@Param("uid") Integer uid);
    Friendship findByPair(@Param("uid1") Integer uid1, @Param("uid2") Integer uid2);
}