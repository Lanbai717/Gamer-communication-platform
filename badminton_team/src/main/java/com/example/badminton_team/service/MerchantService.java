package com.example.badminton_team.service;

import com.example.badminton_team.dto.*;
import com.example.badminton_team.entity.Merchant;

import java.util.List;

public interface MerchantService {
    LoginRespDto login(LoginDto loginDto);
    void register(MerchantRegisterDto registerDto);
    Merchant getMerchantInfo(Integer mid);
    void updateMerchant(Merchant merchant);
    /**
     * 根据状态查询商家列表
     * @param status 商家状态（0-待审核，1-正常，2-禁用）
     * @return 商家列表
     */
    List<Merchant> findByStatus(Integer status);
    // 查询所有商家（若 status 为 null 则返回全部，否则按状态过滤）
    List<Merchant> findAll(Integer status);
    /**
     * 商家状态（0-待提交，1-已提交）
     */
    // 执行审核操作
    void audit(MerchantAuditDto dto);
    PageResult<Merchant> findPage(Integer page, Integer size, String keyword, Integer status);
    void updateStatus(Integer mid, Integer status);
    long countAll();
    List<Merchant> findPending();
    void checkMerchantStatus(Integer mid);

}