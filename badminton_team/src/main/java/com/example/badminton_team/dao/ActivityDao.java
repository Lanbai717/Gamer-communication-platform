package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Activity;
import com.example.badminton_team.entity.vo.ActivityDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ActivityDao {
    /**
     * 根据ID查询活动
     */
    Activity findByAid(@Param("aid") Integer aid);

    /**
     * 查询活动列表（支持多条件筛选）
     */
    List<Activity> findList(@Param("sportType") Integer sportType,
                            @Param("status") Integer status,
                            @Param("startTime") Date startTime,
                            @Param("endTime") Date endTime,
                            @Param("keyword") String keyword);

    /**
     * 查询用户创建的活动
     */
    List<Activity> findByCreatorId(@Param("creatorId") Integer creatorId);

    /**
     * 插入活动
     */
    int insert(Activity activity);

    /**
     * 更新活动信息
     */
    int update(Activity activity);

    /**
     * 更新活动状态（取消、结束等）
     */
    int updateStatus(@Param("aid") Integer aid, @Param("status") Integer status);

    /**
     * 增加当前报名人数
     */
    int increaseCurrentPlayers(@Param("aid") Integer aid);

    /**
     * 减少当前报名人数
     */
    int decreaseCurrentPlayers(@Param("aid") Integer aid);

    /**
     * 查询活动报名用户
     */
    List<ActivityDetailVO.Participant> selectParticipants(@Param("aid") Integer aid);

    Activity selectById(@Param("aid") Integer aid);

    int updateAll(Activity activity);
}