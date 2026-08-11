package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VenueDao {
    /**
     * 根据ID查询场地
     */
    Venue findByVid(@Param("vid") Integer vid);

    /**
     * 查询所有场地（可带条件）
     */
    List<Venue> findAll(@Param("keyword") String keyword, @Param("status") Integer status);

    /**
     * 根据商家ID查询该商家的所有场地
     */
    List<Venue> findByOwnerId(@Param("ownerId") Integer ownerId);

    /**
     * 插入场地
     */
    int insert(Venue venue);

    /**
     * 更新场地信息
     */
    int update(Venue venue);

    /**
     * 更新场地状态
     */
    int updateStatus(@Param("vid") Integer vid, @Param("status") Integer status);


}