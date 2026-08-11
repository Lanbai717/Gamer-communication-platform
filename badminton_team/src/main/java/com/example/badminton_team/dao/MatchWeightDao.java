package com.example.badminton_team.dao;

import com.example.badminton_team.entity.MatchWeight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchWeightDao {

    List<MatchWeight> findAll();

    MatchWeight findByFeature(@Param("featureName") String featureName);
}