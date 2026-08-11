package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantDao {
    /**
     * 根据用户名查询商家
     */
    Merchant findByUsername(@Param("username") String username);

    /**
     * 根据ID查询商家
     */
    Merchant findByMid(@Param("mid") Integer mid);

    /**
     * 插入商家
     */
    int insert(Merchant merchant);

    /**
     * 更新商家信息
     */
    int update(Merchant merchant);

    /**
     * 更新商家状态（审核）
     */
    int updateStatus(@Param("mid") Integer mid, @Param("status") Integer status);

    /**
     * 查询所有商家（管理员用）
     */
    List<Merchant> findAll();

    /**
     * 根据状态查询商家（待审核、正常等）
     */
    List<Merchant> findByStatus(@Param("status") Integer status);

    List<Merchant> findPage(@Param("offset") int offset, @Param("size") int size,
                            @Param("keyword") String keyword, @Param("status") Integer status);
    long count(@Param("keyword") String keyword, @Param("status") Integer status);

    /**
     * 统计商家总数
     */
    long countAll();

    /**
     * 根据状态和是否已提交审核查询商家
     * @param status 商家状态（0-待审核，1-正常，2-禁用）
     * @param isSubmitted 是否已提交审核（0-未提交，1-已提交）
     * @return 商家列表
     */
    List<Merchant> findByStatusAndSubmitted(@Param("status") Integer status, @Param("isSubmitted") Integer isSubmitted);
}