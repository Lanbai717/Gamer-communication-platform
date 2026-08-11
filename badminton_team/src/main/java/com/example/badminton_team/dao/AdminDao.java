package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminDao {
    /**
     * 根据用户名查询管理员
     */
    Admin findByUsername(@Param("username") String username);

    /**
     * 根据ID查询管理员
     */
    Admin findByAid(@Param("aid") Integer aid);

    /**
     * 插入管理员
     */
    int insert(Admin admin);

    /**
     * 更新管理员信息
     */
    int update(Admin admin);

    /**
     * 更新管理员状态
     */
    int updateStatus(@Param("aid") Integer aid, @Param("status") Integer status);
}