package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.MerchantDao;
import com.example.badminton_team.dto.*;
import com.example.badminton_team.entity.Merchant;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.MerchantService;
import com.example.badminton_team.util.JsonUtil;
import com.example.badminton_team.util.JwtUtil;
import com.example.badminton_team.util.PasswordUtil;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final MerchantDao merchantDao;
    private final JwtUtil jwtUtil;

    // 构造器注入，避免字段注入警告
    public MerchantServiceImpl(MerchantDao merchantDao, JwtUtil jwtUtil) {
        this.merchantDao = merchantDao;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginRespDto login(LoginDto loginDto) {
        Merchant merchant = merchantDao.findByUsername(loginDto.getUsername());
        if (merchant == null || !PasswordUtil.matches(loginDto.getPassword(), merchant.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(merchant.getMid(), merchant.getUsername(), "merchant");
        merchant.setPassword(null);
        LoginRespDto resp = new LoginRespDto();
        resp.setToken(token);
        resp.setUserInfo(merchant);
        return resp;
    }

    @Override
    @Transactional
    public void register(MerchantRegisterDto dto) {
        if (merchantDao.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        Merchant merchant = new Merchant();
        merchant.setUsername(dto.getUsername());
        merchant.setPassword(PasswordUtil.encode(dto.getPassword()));
        merchant.setCompanyName(dto.getCompanyName());
        merchant.setContactPerson(dto.getContactPerson());
        merchant.setStatus(0);          // 待审核
        merchant.setIsSubmitted(0);     // 未提交审核
        merchant.setCreateTime(new Date());
        merchant.setUpdateTime(new Date());
        merchantDao.insert(merchant);
    }

    @Override
    public Merchant getMerchantInfo(Integer mid) {
        Merchant merchant = merchantDao.findByMid(mid);
        if (merchant == null) throw new BusinessException("商家不存在");
        // 如果处于待审核状态，不清除 pendingData，前端可以用来展示待审核内容
        merchant.setPassword(null);
        return merchant;
    }

    @Override
    @Transactional
    public void updateMerchant(Merchant merchant) {
        Merchant exist = merchantDao.findByMid(merchant.getMid());
        if (exist == null) throw new BusinessException("商家不存在");

        Map<String, Object> pending = new HashMap<>();

        // 收集所有变化字段（只比较，不修改正式字段）
        if (merchant.getCompanyName() != null && !Objects.equals(merchant.getCompanyName(), exist.getCompanyName())) {
            pending.put("companyName", merchant.getCompanyName());
        }
        if (merchant.getContactPerson() != null && !Objects.equals(merchant.getContactPerson(), exist.getContactPerson())) {
            pending.put("contactPerson", merchant.getContactPerson());
        }
        if (merchant.getContactPhone() != null && !Objects.equals(merchant.getContactPhone(), exist.getContactPhone())) {
            pending.put("contactPhone", merchant.getContactPhone());
        }
        if (merchant.getEmail() != null && !Objects.equals(merchant.getEmail(), exist.getEmail())) {
            pending.put("email", merchant.getEmail());
        }
        if (merchant.getAddress() != null && !Objects.equals(merchant.getAddress(), exist.getAddress())) {
            pending.put("address", merchant.getAddress());
        }
        if (merchant.getBusinessLicense() != null && !Objects.equals(merchant.getBusinessLicense(), exist.getBusinessLicense())) {
            pending.put("businessLicense", merchant.getBusinessLicense());
        }
        if (merchant.getBusinessHours() != null && !Objects.equals(merchant.getBusinessHours(), exist.getBusinessHours())) {
            pending.put("businessHours", merchant.getBusinessHours());
        }
        if (merchant.getFacilities() != null && !Objects.equals(merchant.getFacilities(), exist.getFacilities())) {
            pending.put("facilities", merchant.getFacilities());
        }
        if (merchant.getDetailIntro() != null && !Objects.equals(merchant.getDetailIntro(), exist.getDetailIntro())) {
            pending.put("detailIntro", merchant.getDetailIntro());
        }
        if (merchant.getVideos() != null && !Objects.equals(merchant.getVideos(), exist.getVideos())) {
            pending.put("videos", merchant.getVideos());
        }
        if (merchant.getRules() != null && !Objects.equals(merchant.getRules(), exist.getRules())) {
            pending.put("rules", merchant.getRules());
        }
        if (merchant.getVenuePhotos() != null && !Objects.equals(merchant.getVenuePhotos(), exist.getVenuePhotos())) {
            pending.put("venuePhotos", merchant.getVenuePhotos());
        }

        if (!pending.isEmpty()) {
            exist.setPendingData(JsonUtil.toJson(pending));
            exist.setStatus(0);          // 待审核
            exist.setIsSubmitted(1);     // 已提交
            exist.setUpdateTime(new Date());
            merchantDao.update(exist);
        }
    }

    @Override
    public List<Merchant> findAll(Integer status) {
        if (status != null) {
            return merchantDao.findByStatus(status);
        } else {
            return merchantDao.findAll();
        }
    }

    @Override
    @Transactional
    public void audit(MerchantAuditDto dto) {
        Merchant merchant = merchantDao.findByMid(dto.getMid());
        if (merchant == null) throw new BusinessException("商家不存在");
        if (merchant.getIsSubmitted() != 1) {
            throw new BusinessException("商家未提交审核或已处理");
        }
        if (dto.getStatus() == 1) { // 通过
            if (StringUtils.hasText(merchant.getPendingData())) {
                Map<String, Object> pending = JsonUtil.fromJson(merchant.getPendingData(), Map.class);
                // 将待审核数据应用到正式字段
                if (pending.containsKey("companyName")) merchant.setCompanyName((String) pending.get("companyName"));
                if (pending.containsKey("contactPerson")) merchant.setContactPerson((String) pending.get("contactPerson"));
                if (pending.containsKey("contactPhone")) merchant.setContactPhone((String) pending.get("contactPhone"));
                if (pending.containsKey("email")) merchant.setEmail((String) pending.get("email"));
                if (pending.containsKey("address")) merchant.setAddress((String) pending.get("address"));
                if (pending.containsKey("businessLicense")) merchant.setBusinessLicense((String) pending.get("businessLicense"));
                if (pending.containsKey("businessHours")) merchant.setBusinessHours((String) pending.get("businessHours"));
                if (pending.containsKey("facilities")) merchant.setFacilities((String) pending.get("facilities"));
                if (pending.containsKey("detailIntro")) merchant.setDetailIntro((String) pending.get("detailIntro"));
                if (pending.containsKey("videos")) merchant.setVideos((String) pending.get("videos"));
                if (pending.containsKey("rules")) merchant.setRules((String) pending.get("rules"));
                if (pending.containsKey("venuePhotos")) merchant.setVenuePhotos((String) pending.get("venuePhotos"));
                merchant.setPendingData(null);
            }
            merchant.setStatus(1);
            merchant.setIsSubmitted(0);
        } else { // 拒绝
            merchant.setStatus(2);
            merchant.setPendingData(null);
            merchant.setIsSubmitted(0);
        }
        merchant.setUpdateTime(new Date());
        merchantDao.update(merchant);
    }

    @Override
    public PageResult<Merchant> findPage(Integer page, Integer size, String keyword, Integer status) {
        int offset = (page - 1) * size;
        List<Merchant> list = merchantDao.findPage(offset, size, keyword, status);
        long total = merchantDao.count(keyword, status);
        return new PageResult<>(list, total);
    }

    @Override
    @Transactional
    public void updateStatus(Integer mid, Integer status) {
        merchantDao.updateStatus(mid, status);
    }

    @Override
    public long countAll() {
        return merchantDao.countAll();
    }


    @Override
    public void checkMerchantStatus(Integer mid) {
        Merchant merchant = merchantDao.findByMid(mid);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }
        if (merchant.getStatus() != 1) {
            throw new BusinessException("请先完善资料并等待管理员审核通过");
        }
    }

    @Override
    public List<Merchant> findByStatus(Integer status) {
        return merchantDao.findByStatus(status);
    }

    @Override
    public List<Merchant> findPending() {
        // 返回已提交审核且状态为0的商家（即等待管理员审核的商家）
        return merchantDao.findByStatusAndSubmitted(0, 1);
    }


}