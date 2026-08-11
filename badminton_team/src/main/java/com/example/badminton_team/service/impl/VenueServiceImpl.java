package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.VenueDao;
import com.example.badminton_team.dto.VenueDto;
import com.example.badminton_team.entity.Venue;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    private VenueDao venueDao;

    @Override
    @Transactional
    public void add(VenueDto dto, Integer mid) {
        Venue venue = new Venue();
        venue.setCode(dto.getCode());
        venue.setDescription(dto.getDescription());
        venue.setPricePerHour(dto.getPricePerHour());
        venue.setPhotos(dto.getPhotos());
        venue.setOwnerId(mid);
        venue.setStatus(0); // 默认正常
        venue.setCreateTime(new Date());
        venue.setUpdateTime(new Date());
        venueDao.insert(venue);
    }

    @Override
    @Transactional
    public void update(VenueDto dto, Integer mid) {
        Venue venue = venueDao.findByVid(dto.getVid());
        if (venue == null || !venue.getOwnerId().equals(mid)) {
            throw new BusinessException("无权限修改此场地");
        }
        if (dto.getCode() != null) venue.setCode(dto.getCode());
        if (dto.getDescription() != null) venue.setDescription(dto.getDescription());
        if (dto.getPricePerHour() != null) venue.setPricePerHour(dto.getPricePerHour());
        if (dto.getPhotos() != null) venue.setPhotos(dto.getPhotos());
        venue.setUpdateTime(new Date());
        venueDao.update(venue);
    }

    @Override
    @Transactional
    public void delete(Integer vid, Integer mid) {
        Venue venue = venueDao.findByVid(vid);
        if (venue == null || !venue.getOwnerId().equals(mid)) {
            throw new BusinessException("无权限删除此场地");
        }
        venueDao.updateStatus(vid, 2); // 软删除
    }

    @Override
    public List<Venue> getByMerchant(Integer mid) {
        return venueDao.findByOwnerId(mid);
    }

    @Override
    public List<Venue> listForUser(String keyword, Double lng, Double lat) {
        // 用户端只显示正常状态的场地
        return venueDao.findAll(keyword, 0);
    }

    @Override
    public Venue getDetail(Integer vid) {
        Venue venue = venueDao.findByVid(vid);
        if (venue == null) throw new BusinessException("场地不存在");
        return venue;
    }

    @Override
    public Venue getById(Integer vid) {
        Venue venue = venueDao.findByVid(vid);
        if (venue == null) throw new BusinessException("场地不存在");
        return venue;
    }
}