package com.example.badminton_team.dao;


import com.example.badminton_team.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    User findByUid(@Param("uid") Integer uid);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户信息
     */
    int update(User user);

    /**
     * 更新用户状态
     */
    int updateStatus(@Param("uid") Integer uid, @Param("status") Integer status);

    /**
     * 查询所有用户（管理员用）
     */
    List<User> findAll();

    /**
     * 根据条件搜索用户（可根据昵称、手机号模糊查询）
     */
    List<User> search(@Param("keyword") String keyword);

    /**
     * 邮箱验证
     */
    User findByEmail(@Param("email") String email);

    List<User> findPage(@Param("offset") int offset, @Param("size") int size, @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    /**
     * 统计用户总数
     */
    long countAll();

    /**
     * 查询除指定 uid 外所有状态正常的用户（用于匹配）
     */
    List<User> findAllExcept(@Param("excludeUid") Integer excludeUid);

}
