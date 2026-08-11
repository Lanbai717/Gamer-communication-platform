package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.ActivityDao;
import com.example.badminton_team.dao.UserDao;
import com.example.badminton_team.dao.ActivityParticipantDao;
import com.example.badminton_team.dto.ActivityDto;
import com.example.badminton_team.dto.ActivityUpdateDto;
import com.example.badminton_team.entity.Activity;
import com.example.badminton_team.entity.ActivityParticipant;
import com.example.badminton_team.entity.User;
import com.example.badminton_team.entity.vo.ActivityDetailVO;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ActivityParticipantDao participantDao;

    @Override
    @Transactional
    public void create(ActivityDto dto, Integer uid) {
        Activity activity = new Activity();
        activity.setTitle(dto.getTitle());
        activity.setVenueId(dto.getVenueId());
        activity.setLocation(dto.getLocation());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setCreatorId(uid);
        activity.setMaxPlayers(dto.getMaxPlayers());
        activity.setCurrentPlayers(0);
        activity.setLevelRequire(dto.getLevelRequire());
        activity.setDescription(dto.getDescription());
        activity.setStatus(0); // 招募中
        activity.setCreateTime(new Date());
        activity.setUpdateTime(new Date());
        activityDao.insert(activity);
    }

    @Override
    public List<Activity> list(Integer sportType, String keyword, Integer page, Integer size) {
        return activityDao.findList(sportType, null, null, null, keyword);
    }

    @Override
    public Activity getDetail(Integer aid) {
        Activity activity = activityDao.findByAid(aid);
        if (activity == null) throw new BusinessException("活动不存在");
        return activity;
    }

    @Override
    @Transactional
    public void join(Integer aid, Integer uid) {
        if (participantDao.findByActivityAndUser(aid, uid) != null) {
            throw new BusinessException("您已报名该活动");
        }
        Activity activity = activityDao.findByAid(aid);
        if (activity == null) throw new BusinessException("活动不存在");
        if (activity.getCurrentPlayers() >= activity.getMaxPlayers()) {
            throw new BusinessException("活动已满员");
        }
        ActivityParticipant participant = new ActivityParticipant();
        participant.setActivityId(aid);
        participant.setUserId(uid);
        participant.setStatus(0); // 待确认
        participant.setJoinTime(new Date());
        participantDao.insert(participant);
        activityDao.increaseCurrentPlayers(aid);
    }

    @Override
    @Transactional
    public void cancel(Integer aid, Integer uid) {
        ActivityParticipant participant = participantDao.findByActivityAndUser(aid, uid);
        if (participant == null) {
            throw new BusinessException("您未报名该活动");
        }
        participantDao.delete(participant.getId());
        activityDao.decreaseCurrentPlayers(aid);
    }

    @Override
    public List<Activity> getByCreator(Integer uid) {
        return activityDao.findByCreatorId(uid);
    }

    @Override
    public List<Activity> getByParticipant(Integer uid) {
        List<ActivityParticipant> participants = participantDao.findByUserId(uid);
        return participants.stream()
                .map(p -> activityDao.findByAid(p.getActivityId()))
                .toList();
    }
    @Override
    public ActivityDetailVO getActivityDetail(Integer aid) {
        Activity activity = activityDao.selectById(aid);
        if (activity == null) throw new BusinessException("活动不存在");

        ActivityDetailVO vo = new ActivityDetailVO();
        vo.setAid(activity.getAid());
        vo.setTitle(activity.getTitle());
        vo.setLocation(activity.getLocation());
        vo.setStartTime(activity.getStartTime() != null ? activity.getStartTime().toString() : "");
        vo.setEndTime(activity.getEndTime() != null ? activity.getEndTime().toString() : "");
        vo.setMaxPlayers(activity.getMaxPlayers());
        vo.setCurrentPlayers(activity.getCurrentPlayers());
        vo.setStatus(getStatusText(activity.getStatus())); // 自己写个方法映射状态
        vo.setDescription(activity.getDescription());
        vo.setLevelRequire(activity.getLevelRequire());

        // 发起者信息
        User creator = userDao.findByUid(activity.getCreatorId());
        vo.setCreatorUid(creator.getUid());
        vo.setCreatorNickname(creator.getNickname());
        vo.setCreatorAvatar(creator.getAvatar());

        // 报名列表
        List<ActivityDetailVO.Participant> participants = activityDao.selectParticipants(activity.getAid());
        vo.setParticipants(participants);
        return vo;
    }
    private String getStatusText(int status) {
        switch (status) {
            case 0: return "招募中";
            case 1: return "已满员";
            case 2: return "已取消";
            case 3: return "已结束";
            default: return "未知";
        }
    }

    @Override
    @Transactional
    public void updateActivity(ActivityUpdateDto dto, Integer uid) {
        Activity exist = activityDao.selectById(dto.getAid());
        if (exist == null) throw new BusinessException("活动不存在");
        if (!exist.getCreatorId().equals(uid)) throw new BusinessException("无权编辑");

        // 逐字段赋值（若 DTO 字段为 null 则保留原值）
        exist.setTitle(dto.getTitle() != null ? dto.getTitle() : exist.getTitle());
        exist.setLocation(dto.getLocation() != null ? dto.getLocation() : exist.getLocation());
        exist.setStartTime(dto.getStartTime() != null ? parseDate(dto.getStartTime()) : exist.getStartTime());
        exist.setEndTime(dto.getEndTime() != null ? parseDate(dto.getEndTime()) : exist.getEndTime());
        exist.setMaxPlayers(dto.getMaxPlayers() != null ? dto.getMaxPlayers() : exist.getMaxPlayers());
        exist.setLevelRequire(dto.getLevelRequire() != null ? dto.getLevelRequire() : exist.getLevelRequire());
        exist.setDescription(dto.getDescription() != null ? dto.getDescription() : exist.getDescription());

        // 执行全字段更新（强制更新所有字段，保证数据库变动）
        activityDao.updateAll(exist);
    }

    /**
     * 将字符串 "yyyy-MM-dd HH:mm:ss" 转换为 Date
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        // 尝试多种格式
        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd HH:mm",
                "yyyy/MM/dd HH:mm:ss"
        };
        for (String pattern : patterns) {
            try {
                return new SimpleDateFormat(pattern).parse(dateStr);
            } catch (ParseException ignored) {
            }
        }
        // 如果都失败，尝试用 java.time 解析
        try {
            return Date.from(java.time.LocalDateTime.parse(dateStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new BusinessException("时间格式错误: " + dateStr);
        }
    }
}
