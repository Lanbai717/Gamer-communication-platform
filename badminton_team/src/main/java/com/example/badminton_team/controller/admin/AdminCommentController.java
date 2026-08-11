package com.example.badminton_team.controller.admin;

import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Comment;
import com.example.badminton_team.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取指定帖子的所有评论（用于管理员查看）
     * @param postId 帖子ID
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getCommentsByPost(@PathVariable Integer postId) {
        List<Comment> comments = commentService.getByPost(postId);
        return Result.success(comments);
    }
}