package com.example.badminton_team.controller.user;

import com.example.badminton_team.dto.CommentDto;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Comment;
import com.example.badminton_team.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user/comment")
public class UserCommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public Result<?> create(@Valid @RequestBody CommentDto dto,
                            @RequestAttribute("uid") Integer uid) {
        commentService.create(dto, uid);
        return Result.success("评论成功", null);
    }

    @GetMapping("/post/{postId}")
    public Result<List<Comment>> listByPost(@PathVariable Integer postId) {
        return Result.success(commentService.getByPost(postId));
    }

    @DeleteMapping("/{cid}")
    public Result<?> delete(@PathVariable Integer cid, @RequestAttribute("uid") Integer uid) {
        commentService.delete(cid, uid);
        return Result.success("删除成功", null);
    }
}