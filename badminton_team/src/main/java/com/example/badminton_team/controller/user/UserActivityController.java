package com.example.badminton_team.controller.user;

import com.example.badminton_team.dto.ActivityDto;
import com.example.badminton_team.dto.ActivityUpdateDto;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Activity;
import com.example.badminton_team.entity.vo.ActivityDetailVO;
import com.example.badminton_team.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user/activity")
public class UserActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/create")
    public Result<?> create(@Valid @RequestBody ActivityDto dto,
                            @RequestAttribute("uid") Integer uid) {
        activityService.create(dto, uid);
        return Result.success("创建成功", null);
    }

    @GetMapping("/list")
    public Result<List<Activity>> list(@RequestParam(required = false) Integer sportType,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(activityService.list(sportType, keyword, page, size));
    }

    @GetMapping("/{aid}")
    public Result<Activity> detail(@PathVariable Integer aid) {
        return Result.success(activityService.getDetail(aid));
    }

    @PostMapping("/join/{aid}")
    public Result<?> join(@PathVariable Integer aid, @RequestAttribute("uid") Integer uid) {
        activityService.join(aid, uid);
        return Result.success("报名成功", null);
    }

    @DeleteMapping("/cancel/{aid}")
    public Result<?> cancel(@PathVariable Integer aid, @RequestAttribute("uid") Integer uid) {
        activityService.cancel(aid, uid);
        return Result.success("已取消", null);
    }

    @GetMapping("/my/created")
    public Result<List<Activity>> myCreated(@RequestAttribute("uid") Integer uid) {
        return Result.success(activityService.getByCreator(uid));
    }

    @GetMapping("/my/joined")
    public Result<List<Activity>> myJoined(@RequestAttribute("uid") Integer uid) {
        return Result.success(activityService.getByParticipant(uid));
    }

    @GetMapping("/{aid}/detail")
    public Result<ActivityDetailVO> getActivityDetail(@PathVariable Integer aid) {
        return Result.success(activityService.getActivityDetail(aid));
    }

    @PutMapping("/update")
    public Result<?> updateActivity(@Valid @RequestBody ActivityUpdateDto dto,
                                    @RequestAttribute("uid") Integer uid) {
        activityService.updateActivity(dto, uid);
        return Result.success("更新成功");
    }
}
