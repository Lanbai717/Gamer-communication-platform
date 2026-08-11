package com.example.badminton_team.controller.admin;

import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Post;
import com.example.badminton_team.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/post")
public class AdminPostController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子列表（支持分页、按状态筛选、关键字搜索）
     * @param page 页码（默认1）
     * @param size 每页大小（默认10）
     * @param status 状态（可选，0-正常，1-待审核，2-已删除）
     * @param keyword 搜索关键字（可选）
     */
    @GetMapping("/list")
    public Result<PageResult<Post>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        PageResult<Post> pageResult = postService.findPage(page, size, status, keyword);
        return Result.success(pageResult);
    }

    /**
     * 获取待审核帖子列表（分页、搜索）
     * 为兼容前端，调用 /list 并固定 status=1
     */
    @GetMapping("/pending")
    public Result<PageResult<Post>> pending(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        // 固定 status=1 表示待审核
        PageResult<Post> pageResult = postService.findPage(page, size, 1, keyword);
        return Result.success(pageResult);
    }

    /**
     * 审核帖子（通过/拒绝）
     * @param pid 帖子ID
     * @param status 0-通过，2-拒绝
     */
    @PutMapping("/audit/{pid}")
    public Result<?> audit(@PathVariable Integer pid, @RequestParam Integer status) {
        postService.updateStatus(pid, status);
        return Result.success("审核成功", null);
    }

    /**
     * 删除帖子（物理删除）
     * @param pid 帖子ID
     */
    @DeleteMapping("/{pid}")
    public Result<?> delete(@PathVariable Integer pid) {
        postService.deleteByAdmin(pid);
        return Result.success("删除成功", null);
    }

    @PutMapping("/ban/{pid}")
    public Result<?> ban(@PathVariable Integer pid) {
        postService.banPost(pid);
        return Result.success("封禁成功", null);
    }

    @PutMapping("/unban/{pid}")
    public Result<?> unban(@PathVariable Integer pid) {
        postService.unbanPost(pid);
        return Result.success("解封成功", null);
    }

    @GetMapping("/{pid}")
    public Result<Post> getDetail(@PathVariable Integer pid) {
        Post post = postService.getDetail(pid);
        return Result.success(post);
    }
}