package com.example.badminton_team.service;

import com.example.badminton_team.dto.ActivityDto;
import com.example.badminton_team.entity.Activity;
import com.example.badminton_team.entity.vo.ActivityDetailVO;
import com.example.badminton_team.dto.ActivityUpdateDto;

import java.util.List;

public interface ActivityService {
    void create(ActivityDto dto, Integer uid);
    List<Activity> list(Integer sportType, String keyword, Integer page, Integer size);
    Activity getDetail(Integer aid);
    void join(Integer aid, Integer uid);
    void cancel(Integer aid, Integer uid);
    List<Activity> getByCreator(Integer uid);
    List<Activity> getByParticipant(Integer uid);
    ActivityDetailVO getActivityDetail(Integer aid);
    void updateActivity(ActivityUpdateDto dto, Integer uid);
}
