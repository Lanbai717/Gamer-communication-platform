package com.example.badminton_team.controller.user;

import com.example.badminton_team.entity.vo.UserMatchVO;
import com.example.badminton_team.service.impl.UserMatchingServiceImpl;
import com.example.badminton_team.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/match")
public class MatchController {

    @Autowired
    private UserMatchingServiceImpl matchingService;

    @GetMapping("/recommend")
    public Result<List<UserMatchVO>> recommend(@RequestAttribute("uid") Integer uid) {
        List<UserMatchVO> list = matchingService.getMatches(uid, 10);
        return Result.success(list);
    }
}