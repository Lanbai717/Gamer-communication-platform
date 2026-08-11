package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.MatchWeightDao;
import com.example.badminton_team.dao.UserDao;
import com.example.badminton_team.entity.MatchWeight;
import com.example.badminton_team.entity.User;
import com.example.badminton_team.entity.vo.UserMatchVO;
import com.example.badminton_team.enums.LevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserMatchingServiceImpl {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MatchWeightDao matchWeightMapper;

    /**
     * 为指定用户推荐球友
     * @param uid 当前用户ID
     * @param topN 推荐数量
     */
    public List<UserMatchVO> getMatches(Integer uid, int topN) {
        User target = userDao.findByUid(uid);
        if (target == null) return Collections.emptyList();

        // 所有其他正常用户
        List<User> candidates = userDao.findAllExcept(uid);

        // 获取权重（若表无数据则使用默认值）
        double wSkill = getWeight("skill");
        double wGeo = getWeight("geo");
        double wActive = getWeight("active");
        double totalWeight = wSkill + wGeo + wActive;
        if (totalWeight == 0) totalWeight = 1.0; // 避免除零

        int targetRank = LevelEnum.toInt(target.getSkillLevel());

        List<UserMatchVO> matches = new ArrayList<>();
        for (User cand : candidates) {
            int candRank = LevelEnum.toInt(cand.getSkillLevel());
            // 1. 技能匹配度（0-7 差最大为7）
            double skillSim = 1.0 - Math.abs(targetRank - candRank) / 7.0;

            // 2. 地理匹配度
            double geoSim = locationMatch(target, cand);

            // 3. 活跃度（注册天数 / 365，上限1）
            long days = daysSince(cand.getCreateTime());
            double activeSim = Math.min(days / 365.0, 1.0);

            // 加权总分
            double score = (wSkill * skillSim + wGeo * geoSim + wActive * activeSim) / totalWeight;
            score = Math.round(score * 10000.0) / 10000.0;

            UserMatchVO vo = new UserMatchVO();
            vo.setUid(cand.getUid());
            vo.setNickname(cand.getNickname());
            vo.setAvatar(cand.getAvatar());
            vo.setLevelText(LevelEnum.toText(candRank));
            vo.setLocation(buildLocation(cand));
            vo.setScore(score);
            matches.add(vo);
        }

        // 按得分降序，取前 topN
        matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return matches.stream().limit(topN).collect(Collectors.toList());
    }

    /** 地理匹配得分：同区1.0，同市0.8，同省0.5，其它0.1，未填0.3 */
    private double locationMatch(User u1, User u2) {
        String p1 = u1.getProvince(), p2 = u2.getProvince();
        String c1 = u1.getCity(), c2 = u2.getCity();
        String d1 = u1.getDistrict(), d2 = u2.getDistrict();
        if (isEmpty(p1) || isEmpty(p2)) return 0.3;
        if (p1.equals(p2)) {
            if (!isEmpty(c1) && !isEmpty(c2) && c1.equals(c2)) {
                if (!isEmpty(d1) && !isEmpty(d2) && d1.equals(d2)) return 1.0;
                return 0.8;
            }
            return 0.5;
        }
        return 0.1;
    }

    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }

    private String buildLocation(User u) {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty(u.getProvince())) sb.append(u.getProvince()).append(" ");
        if (!isEmpty(u.getCity())) sb.append(u.getCity()).append(" ");
        if (!isEmpty(u.getDistrict())) sb.append(u.getDistrict());
        return sb.toString().trim();
    }

    private long daysSince(Date date) {
        if (date == null) return 0;
        LocalDateTime dateTime = date.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Duration.between(dateTime, LocalDateTime.now()).toDays();
    }

    private double getWeight(String feature) {
        MatchWeight mw = matchWeightMapper.findByFeature(feature);
        return mw != null ? mw.getWeight() : 0.3;
    }
}