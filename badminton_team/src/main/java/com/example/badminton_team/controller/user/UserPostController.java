package com.example.badminton_team.controller.user;

import com.example.badminton_team.dto.PostDto;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Post;
import com.example.badminton_team.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user/post")
public class UserPostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public Result<?> create(@Valid @RequestBody PostDto dto,
                            @RequestAttribute("uid") Integer uid) {
        postService.create(dto, uid);
        return Result.success("发布成功", null);
    }

    @GetMapping("/list")
    public Result<List<Post>> list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(postService.list(page, size));
    }

    @GetMapping("/{pid}")
    public Result<Post> detail(@PathVariable Integer pid) {
        return Result.success(postService.getDetail(pid));
    }

    @PostMapping("/like/{pid}")
    public Result<Map<String, Object>> like(@PathVariable Integer pid, @RequestAttribute("uid") Integer uid) {
        Map<String, Object> result = postService.toggleLike(pid, uid);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<List<Post>> myPosts(@RequestAttribute("uid") Integer uid) {
        return Result.success(postService.getByUser(uid));
    }
}
