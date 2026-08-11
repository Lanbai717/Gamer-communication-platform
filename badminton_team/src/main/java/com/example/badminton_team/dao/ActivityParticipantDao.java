package com.example.badminton_team.dao;

import com.example.badminton_team.entity.ActivityParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityParticipantDao {
    /**
     * 查询活动的所有参与者
     */
    List<ActivityParticipant> findByActivityId(@Param("activityId") Integer activityId);

    /**
     * 查询用户参与的所有活动
     */
    List<ActivityParticipant> findByUserId(@Param("userId") Integer userId);

    /**
     * 查询用户是否已报名某活动
     */
    ActivityParticipant findByActivityAndUser(@Param("activityId") Integer activityId, @Param("userId") Integer userId);

    /**
     * 插入报名记录
     */
    int insert(ActivityParticipant participant);

    /**
     * 更新报名状态（确认/取消）
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 删除报名记录（取消报名）
     */
    int delete(@Param("id") Integer id);
}